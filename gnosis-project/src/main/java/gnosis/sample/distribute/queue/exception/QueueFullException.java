package gnosis.sample.distribute.queue.exception;

/**
 * 队列满异常
 * 当队列达到最大长度限制时抛出此异常
 */
public class QueueFullException extends RuntimeException {
    private final String queueName;
    private final int currentSize;
    private final int maxSize;

    public QueueFullException(String queueName, int currentSize, int maxSize) {
        super("Queue '" + queueName + "' is full: " + currentSize + " >= " + maxSize);
        this.queueName = queueName;
        this.currentSize = currentSize;
        this.maxSize = maxSize;
    }

    public String getQueueName() { 
        return queueName; 
    }
    
    public int getCurrentSize() { 
        return currentSize; 
    }
    
    public int getMaxSize() { 
        return maxSize; 
    }

    @Override
    public String toString() {
        return "QueueFullException{" +
                "queueName='" + queueName + '\'' +
                ", currentSize=" + currentSize +
                ", maxSize=" + maxSize +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}