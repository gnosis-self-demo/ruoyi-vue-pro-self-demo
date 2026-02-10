package gnosis.sample.distribute.queue.enums;

/**
 * 队列消息状态枚举
 */
public enum QueueMessageStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    DONE("done");

    private final String value;

    private QueueMessageStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QueueMessageStatus fromValue(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Status value cannot be null or empty");
        }
        for (QueueMessageStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}