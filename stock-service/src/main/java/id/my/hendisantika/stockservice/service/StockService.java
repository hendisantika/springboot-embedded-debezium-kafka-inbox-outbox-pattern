package id.my.hendisantika.stockservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.stockservice.dto.event.StockCreateEvent;
import id.my.hendisantika.stockservice.dto.response.OrderDebeziumResponse;
import id.my.hendisantika.stockservice.entity.ProductOutbox;
import id.my.hendisantika.stockservice.entity.Products;
import id.my.hendisantika.stockservice.entity.enums.Operation;
import id.my.hendisantika.stockservice.entity.enums.ProductOutboxStatus;
import id.my.hendisantika.stockservice.repository.ProductOutboxRepository;
import id.my.hendisantika.stockservice.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

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

    @SneakyThrows
    public void stockEventCreate(OrderDebeziumResponse response) {
        StockCreateEvent stockCreateEvent = mapper.readValue(response.getPayload(), StockCreateEvent.class);

        Optional<Products> productStock = productStockRepository.findByProductId(stockCreateEvent.getProductId());

        if (productStock.isPresent()) {
            if (productStock.get().getStockSize() < 1) {
                productOutboxRepository.save(toOutboxEntity(productStock.get(), ProductOutboxStatus.FAILED, "orderFailed", "notAvaliableStock", stockCreateEvent.getOrderId()));
                return;
            }

            if (stockCreateEvent.getTotalAmount().compareTo(productStock.get().getTotalAmount()) < 0) {
                productOutboxRepository.save(toOutboxEntity(productStock.get(), ProductOutboxStatus.FAILED, "orderFailed", "amountNotEnough", stockCreateEvent.getOrderId()));
                return;
            }

            Integer updateVersion = productStockRepository.updateProductStock(productStock.get().getId(), productStock.get().getVersion());
            if (updateVersion < 1) {
                productOutboxRepository.save(toOutboxEntity(productStock.get(), ProductOutboxStatus.FAILED, "orderFailed", "versionChange", stockCreateEvent.getOrderId()));
                return;
            }

            productOutboxRepository.save(toOutboxEntity(productStock.get(), ProductOutboxStatus.DONE, "orderCompleted", "successfull", stockCreateEvent.getOrderId()));

        } else {
            productOutboxRepository.save(toOutboxEntityNoProduct(stockCreateEvent, ProductOutboxStatus.FAILED, "orderFailed", "noProduct"));
        }
    }

    private ProductOutbox toOutboxEntity(Products productStock, ProductOutboxStatus productOutboxStatus, String aggregateType, String eventType, Long orderId) {
        String payload = null;
        String idempotentKey = RandomStringUtils.randomAlphanumeric(10);
        try {

            payload = mapper.writeValueAsString(productStock);

        } catch (JsonProcessingException ex) {
            log.error("Object could not convert to String. Object: {}", productStock.toString());
            throw new RuntimeException(ex);
        }

        return ProductOutbox.builder()
                .status(productOutboxStatus)
                .createdDate(LocalDateTime.now())
                .idempotentKey(idempotentKey)
                .payload(payload)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .orderId(orderId)
                .build();
    }

    private ProductOutbox toOutboxEntityNoProduct(StockCreateEvent stockCreateEvent, ProductOutboxStatus productOutboxStatus, String aggregateType, String eventType) {
        String payload = null;
        String idempotentKey = RandomStringUtils.randomAlphanumeric(10);
        try {

            payload = mapper.writeValueAsString(stockCreateEvent);

        } catch (JsonProcessingException ex) {
            log.error("Object could not convert to String. Object: {}", stockCreateEvent.toString());
            throw new RuntimeException(ex);
        }

        return ProductOutbox.builder()
                .status(productOutboxStatus)
                .createdDate(LocalDateTime.now())
                .idempotentKey(idempotentKey)
                .payload(payload)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .orderId(stockCreateEvent.getOrderId())
                .build();
    }
}
