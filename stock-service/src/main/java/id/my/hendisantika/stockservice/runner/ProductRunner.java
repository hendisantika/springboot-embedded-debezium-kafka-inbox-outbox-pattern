package id.my.hendisantika.stockservice.runner;

import id.my.hendisantika.stockservice.entity.Products;
import id.my.hendisantika.stockservice.repository.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 08:09
 * To change this template use File | Settings | File Templates.
 */
@Component
@RequiredArgsConstructor
public class ProductRunner implements CommandLineRunner {

    private final ProductsRepository productStockRepository;

    @Override
    public void run(String... args) {
        productStockRepository.deleteAll();

        Products product = Products.builder()
                .productId(10L)
                .createdDate(LocalDateTime.now())
                .version(10)
                .stockSize(10)
                .totalAmount(new BigDecimal("100.20"))
                .build();

        productStockRepository.save(product);

    }
}
