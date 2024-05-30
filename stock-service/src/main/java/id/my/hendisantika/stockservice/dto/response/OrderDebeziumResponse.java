package id.my.hendisantika.stockservice.dto.response;

import id.my.hendisantika.stockservice.entity.enums.ProductOutboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 08:14
 * To change this template use File | Settings | File Templates.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDebeziumResponse {
    private Long id;
    private String aggregate_type;
    private Long created_date;
    private String event_type;
    private String idempotent_key;
    private String payload;
    private ProductOutboxStatus status;
    private Long order_id;
}
