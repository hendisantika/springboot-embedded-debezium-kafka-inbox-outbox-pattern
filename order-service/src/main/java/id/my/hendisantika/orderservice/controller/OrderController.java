package id.my.hendisantika.orderservice.controller;

import id.my.hendisantika.orderservice.dto.request.OrderRequestDto;
import id.my.hendisantika.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:52
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return new ResponseEntity<>(
                orderService.createOrder(orderRequestDto), HttpStatus.CREATED
        );
    }
}
