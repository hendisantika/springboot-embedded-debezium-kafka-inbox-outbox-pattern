package id.my.hendisantika.orderservice.entity.enums;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-embedded-debezium-kafka-inbox-outbox-pattern
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 5/30/24
 * Time: 07:33
 * To change this template use File | Settings | File Templates.
 */
public enum Operation {
    READ("r"),
    CREATE("c"),
    UPDATE("u"),
    DELETE("d");

    private final String code;

    Operation(String code) {
        this.code = code;
    }

    public static Operation forCode(String code) {
        Operation[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            Operation op = var1[var3];
            if (op.code().equalsIgnoreCase(code)) {
                return op;
            }
        }
        return null;
    }

    public String code() {
        return this.code;
    }
}
