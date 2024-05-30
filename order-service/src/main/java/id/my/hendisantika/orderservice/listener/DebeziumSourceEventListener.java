package id.my.hendisantika.orderservice.listener;

import id.my.hendisantika.orderservice.entity.enums.Operation;
import id.my.hendisantika.orderservice.service.OutboxService;
import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.OPERATION;
import static java.util.stream.Collectors.toMap;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:35
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@Component
public class DebeziumSourceEventListener {

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final EmbeddedEngine engine;
    private final OutboxService outboxService;

    private DebeziumSourceEventListener(Configuration sagaConnector, OutboxService outboxService) {
        this.engine = EmbeddedEngine
                .create()
                .using(sagaConnector)
                .notifying(this::handleEvent).build();

        this.outboxService = outboxService;
    }

    @PostConstruct
    private void start() {
        this.executor.execute(engine);
    }

    @PreDestroy
    private void stop() {
        if (this.engine != null) {
            this.engine.stop();
        }
    }

    private void handleEvent(SourceRecord sourceRecord) {
        Struct sourceRecordValue = (Struct) sourceRecord.value();

        if (sourceRecordValue != null) {
            Operation operation = Operation.forCode((String) sourceRecordValue.get(OPERATION));

            if (operation == Operation.CREATE) {

                Struct struct = (Struct) sourceRecordValue.get(AFTER);
                Map<String, Object> payload = struct.schema().fields().stream()
                        .map(Field::name)
                        .filter(fieldName -> struct.get(fieldName) != null)
                        .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                        .collect(toMap(Pair::getKey, Pair::getValue));

                log.info("Data Changed: {} with Operation: {}", payload, operation.name());
                outboxService.maintainReadModel(payload, operation);
            }
        }
    }
}
