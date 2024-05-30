package id.my.hendisantika.stockservice.repository;

import id.my.hendisantika.stockservice.entity.ProductInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 08:08
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface ProductInboxRepository extends JpaRepository<ProductInbox, Long> {

    Optional<ProductInbox> findByIdempotentKey(String idempotentKey);
}
