package id.my.hendisantika.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.orderservice.dto.request.OrderRequestDto;
import id.my.hendisantika.orderservice.entity.Order;
import id.my.hendisantika.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
