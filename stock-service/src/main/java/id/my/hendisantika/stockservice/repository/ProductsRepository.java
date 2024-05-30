package id.my.hendisantika.stockservice.repository;

import id.my.hendisantika.stockservice.entity.Products;
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
 * Time: 08:09
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {

    Optional<Products> findByProductId(Long productId);


    @Modifying
    @Transactional
    @Query(value = "UPDATE products SET version = version + 1,stock_size = stock_size - 1 " +
            "WHERE id =:id AND version =:version", nativeQuery = true)
    Integer updateProductStock(@Param("id") Long id, @Param("version") Integer version);
}
