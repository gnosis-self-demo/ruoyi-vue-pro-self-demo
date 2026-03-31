package com.gnosis.distribute.queue.exception;

/**
 * 队列不存在异常
 */
public class QueueNotFoundException extends Exception {
    public QueueNotFoundException(String message) {
        super(message);
    }
    
    public QueueNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}