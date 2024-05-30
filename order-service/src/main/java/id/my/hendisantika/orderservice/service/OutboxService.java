package id.my.hendisantika.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.orderservice.dto.response.OrderDebeziumResponse;
import id.my.hendisantika.orderservice.entity.OrderOutbox;
import id.my.hendisantika.orderservice.entity.enums.Operation;
import id.my.hendisantika.orderservice.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:32
 * To change this template use File | Settings | File Templates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OrderOutboxRepository orderOutboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void save(OrderOutbox orderOutbox) {
        orderOutboxRepository.save(orderOutbox);
    }

    @SneakyThrows
    public void maintainReadModel(Map<String, Object> productData, Operation operation) {
        final OrderDebeziumResponse response = mapper.convertValue(productData, OrderDebeziumResponse.class);
        kafkaTemplate.send("order-created", response.getIdempotent_key(), mapper.writeValueAsString(response));
    }
}
