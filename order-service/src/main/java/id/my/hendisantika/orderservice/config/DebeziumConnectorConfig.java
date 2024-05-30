package id.my.hendisantika.orderservice.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:45
 * To change this template use File | Settings | File Templates.
 */
@Configuration
public class DebeziumConnectorConfig {
    @Bean
    public io.debezium.config.Configuration sagaConnector() {
        return io.debezium.config.Configuration.create()
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/Users/hendisantika/IdeaProjects/springboot-embedded-debezium-kafka-inbox-outbox-pattern/saga-offset.dat")
                .with("offset.flush.interval.ms", 60000)
                .with("name", "saga-postgres-connector")
                .with("database.server.name", "saga-outbox-server")
                .with("database.hostname", "localhost")
                .with("database.port", "5433")
                .with("database.user", "saga")
                .with("database.password", "saga")
                .with("database.dbname", "saga")
                .with("topic.prefix", "test-order")
                .with("decimal.handling.mode", "string")
                .with("wal_level", "logical")
                .with("plugin.name", "pgoutput")
                .with("table.include.list", "public.order_outbox")
                .with("slot.name", RandomStringUtils.randomAlphabetic(5).toLowerCase()).build();
    }
}
