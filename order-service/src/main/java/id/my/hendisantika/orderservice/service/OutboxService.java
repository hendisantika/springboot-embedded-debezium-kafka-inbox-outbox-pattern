package id.my.hendisantika.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.my.hendisantika.orderservice.entity.OrderOutbox;
import id.my.hendisantika.orderservice.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
}
