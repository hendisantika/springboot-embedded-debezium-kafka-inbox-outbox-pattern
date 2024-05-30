package id.my.hendisantika.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.orderservice.dto.event.StockCreateEvent;
import id.my.hendisantika.orderservice.dto.request.OrderRequestDto;
import id.my.hendisantika.orderservice.entity.Order;
import id.my.hendisantika.orderservice.entity.OrderOutbox;
import id.my.hendisantika.orderservice.entity.enums.OrderOutboxStatus;
import id.my.hendisantika.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:31
 * To change this template use File | Settings | File Templates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Boolean createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderRepository.save(convertToOrder(orderRequestDto));
        outboxService.save(toOutboxEntity(order));
        return true;
    }

    private OrderOutbox toOutboxEntity(Order order) {
        String payload = null;
        String idempotentKey = RandomStringUtils.randomAlphanumeric(10);
        try {
            StockCreateEvent stockCreateEvent = StockCreateEvent.builder()
                    .orderId(order.getId())
                    .productId(order.getProductId())
                    .idempotentKey(idempotentKey)
                    .totalAmount(order.getTotalAmount())
                    .build();

            payload = objectMapper.writeValueAsString(stockCreateEvent);

        } catch (JsonProcessingException ex) {
            log.error("Object could not convert to String. Object: {}", order);
            throw new RuntimeException(ex);
        }

        return OrderOutbox.builder()
                .status(OrderOutboxStatus.CREATED)
                .createdDate(LocalDateTime.now())
                .idempotentKey(idempotentKey)
                .payload(payload)
                .aggregateType("Order")
                .eventType("OrderCreated")
                .orderId(order.getId())
                .build();
    }
}
