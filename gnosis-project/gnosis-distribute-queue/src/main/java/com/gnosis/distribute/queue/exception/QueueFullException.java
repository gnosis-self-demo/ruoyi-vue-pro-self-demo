package com.gnosis.distribute.queue.exception;

/**
 * 队列已满异常
 */
public class QueueFullException extends Exception {
    private final String queueName;
    private final int currentSize;
    private final int maxSize;

    public QueueFullException(String queueName, int currentSize, int maxSize) {
        super(String.format("队列 '%s' 已满，当前大小: %d, 最大大小: %d", queueName, currentSize, maxSize));
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
}