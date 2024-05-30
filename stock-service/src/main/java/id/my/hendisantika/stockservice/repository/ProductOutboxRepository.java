package id.my.hendisantika.stockservice.repository;

import id.my.hendisantika.stockservice.entity.ProductOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
public interface ProductOutboxRepository extends JpaRepository<ProductOutbox, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_outbox  WHERE id =:id", nativeQuery = true)
    void deleteProductOutbox(@Param("id") Long id);
}
