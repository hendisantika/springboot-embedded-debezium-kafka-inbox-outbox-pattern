package id.my.hendisantika.orderservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.orderservice.dto.response.OrderDebeziumResponse;
import id.my.hendisantika.orderservice.entity.Order;
import id.my.hendisantika.orderservice.entity.OrderInbox;
import id.my.hendisantika.orderservice.entity.OrderOutbox;
import id.my.hendisantika.orderservice.entity.enums.OrderOutboxStatus;
import id.my.hendisantika.orderservice.entity.enums.OrderStatus;
import id.my.hendisantika.orderservice.repository.OrderInboxRepository;
import id.my.hendisantika.orderservice.repository.OrderOutboxRepository;
import id.my.hendisantika.orderservice.repository.OrderRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:50
 * To change this template use File | Settings | File Templates.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final OrderInboxRepository orderInboxRepository;

    @KafkaListener(topics = "product-update-fail", groupId = "fail", containerFactory = "orderFailFactory")
    public void handleFailOrder(@Payload String message,
                                @Header(KafkaHeaders.RECEIVED_KEY) @NonNull String idempotentKey,
                                Acknowledgment acknowledgment) throws JsonProcessingException {

        log.info("message {}", message);
        log.info("key {}", idempotentKey);

        if (orderInboxRepository.findByIdempotentKey(idempotentKey).isPresent()) {
            log.error("Stock inbox not created, {} already exist!", idempotentKey);
            acknowledgment.acknowledge();
            return;
        }

        final OrderDebeziumResponse response = objectMapper.readValue(message, OrderDebeziumResponse.class);

        Optional<Order> order = orderRepository.findById(response.getOrder_id());

        if (order.isPresent()) {
            order.get().setOrderStatus(OrderStatus.FAILED);
            order.get().setDescription(response.getEvent_type());
            orderRepository.save(order.get());

            Optional<OrderOutbox> orderOutbox = orderOutboxRepository.findByOrderId(order.get().getId());
            if (orderOutbox.isPresent()) {
                orderOutbox.get().setStatus(OrderOutboxStatus.DONE);
                orderOutboxRepository.save(orderOutbox.get());
            }

        }
        orderInboxRepository.save(OrderInbox.builder().payload(message).createdDate(LocalDateTime.now())
                .idempotentKey(idempotentKey).build());

        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "product-update-successfully", groupId = "success", containerFactory = "orderSuccessFactory")
    public void handleSuccessOrder(@Payload String message,
                                   @Header(KafkaHeaders.RECEIVED_KEY) @NonNull String idempotentKey,
                                   Acknowledgment acknowledgment) throws JsonProcessingException {

        log.info("message {}", message);
        log.info("key {}", idempotentKey);

        if (orderInboxRepository.findByIdempotentKey(idempotentKey).isPresent()) {
            log.error("Stock inbox not created, {} already exist!", idempotentKey);
            acknowledgment.acknowledge();
            return;
        }

        final OrderDebeziumResponse response = objectMapper.readValue(message, OrderDebeziumResponse.class);

        Optional<Order> order = orderRepository.findById(response.getOrder_id());

        if (order.isPresent()) {
            order.get().setOrderStatus(OrderStatus.COMPLETED);
            order.get().setDescription(response.getEvent_type());
            orderRepository.save(order.get());

            Optional<OrderOutbox> orderOutbox = orderOutboxRepository.findByOrderId(order.get().getId());
            if (orderOutbox.isPresent()) {
                orderOutbox.get().setStatus(OrderOutboxStatus.DONE);
                orderOutboxRepository.save(orderOutbox.get());
            }

        }
        orderInboxRepository.save(OrderInbox.builder().payload(message).createdDate(LocalDateTime.now())
                .idempotentKey(idempotentKey).build());

        acknowledgment.acknowledge();
    }
}
