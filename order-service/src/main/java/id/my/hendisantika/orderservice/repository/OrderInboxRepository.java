package id.my.hendisantika.orderservice.repository;

import id.my.hendisantika.orderservice.entity.OrderInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:27
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface OrderInboxRepository extends JpaRepository<OrderInbox, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM order_inbox  WHERE id =:id", nativeQuery = true)
    void deleteOrderInbox(@Param("id") Long id);

    Optional<OrderInbox> findByIdempotentKey(String idempotentKey);
}
