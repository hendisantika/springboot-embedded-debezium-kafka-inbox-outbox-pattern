package id.my.hendisantika.orderservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.orderservice.repository.OrderInboxRepository;
import id.my.hendisantika.orderservice.repository.OrderOutboxRepository;
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
}
