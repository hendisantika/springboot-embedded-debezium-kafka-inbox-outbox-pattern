package id.my.hendisantika.stockservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.stockservice.entity.enums.Operation;
import id.my.hendisantika.stockservice.entity.enums.ProductOutboxStatus;
import id.my.hendisantika.stockservice.repository.ProductOutboxRepository;
import id.my.hendisantika.stockservice.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
 * Time: 08:10
 * To change this template use File | Settings | File Templates.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final ProductsRepository productStockRepository;
    private final ProductOutboxRepository productOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public void maintainReadModel(Map<String, Object> productData, Operation operation) {
        final OrderDebeziumResponse response = mapper.convertValue(productData, OrderDebeziumResponse.class);
        String topic = StringUtils.EMPTY;
        if (response.getStatus() == ProductOutboxStatus.DONE)
            topic = "product-update-successfully";
        else
            topic = "product-update-fail";

        kafkaTemplate.send(topic, response.getIdempotent_key(), mapper.writeValueAsString(response));
    }
}
