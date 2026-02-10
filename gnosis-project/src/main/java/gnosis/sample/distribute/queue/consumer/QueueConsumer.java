package gnosis.sample.distribute.queue.consumer;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.dto.BusinessProcessRequest;
import gnosis.sample.distribute.queue.dto.BusinessProcessResult;
import gnosis.sample.distribute.queue.model.QueueMessage;
import gnosis.sample.distribute.queue.processor.BusinessProcessorManager;
import gnosis.sample.distribute.queue.service.DistributedQueueService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 队列消费者
 * 自动监听并处理队列中的消息
 */
@Slf4j
public class QueueConsumer {

    private DistributedQueueService queueService;

    private RuntimeQueueConfig runtimeConfig;
    
    private BusinessProcessorManager processorManager;

    // 消费者线程控制
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread consumerThread;
    private String currentQueueName;
    private String currentConsumerId;

    // setter方法，用于工厂注入
    public void setQueueService(DistributedQueueService queueService) {
        this.queueService = queueService;
    }

    public void setRuntimeConfig(RuntimeQueueConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    /**
     * 启动消费者线程
     */
    public void startConsumer(String queueName) {
        if (running.compareAndSet(false, true)) {
            this.currentQueueName = queueName;
            this.currentConsumerId = queueName + "-worker-" + System.currentTimeMillis();
            
            this.consumerThread = new Thread(() -> consumeLoop(currentQueueName, currentConsumerId));
            consumerThread.setDaemon(true);
            consumerThread.setName("QueueConsumer-" + currentQueueName);
            consumerThread.start();
            
            log.info("启动消费者线程: {} (consumerId: {})", currentQueueName, currentConsumerId);
        }
    }

    /**
     * 停止消费者线程
     */
    public void stopConsumer() {
        if (running.compareAndSet(true, false)) {
            if (consumerThread != null && consumerThread.isAlive()) {
                consumerThread.interrupt();
                try {
                    consumerThread.join(5000); // 等待最多5秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            log.info("停止消费者线程: {}", currentQueueName);
        }
    }

    /**
     * 消费循环
     */
    private void consumeLoop(String queueName, String consumerId) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 尝试从队列出队消息
                QueueMessage msg = queueService.dequeue(queueName, consumerId);
                
                if (msg != null) {
                    // 解析消息内容
                    String requestId = null;
                    String actualPayload = msg.getMessageBody();
                    boolean hasRequestId = false;

                    // 检查是否为同步消息（包含requestId）
                    if (msg.getMessageBody() != null && msg.getMessageBody().startsWith("{\"requestId\":")) {
                        int start = msg.getMessageBody().indexOf("\"requestId\":\"") + 13;
                        int end = msg.getMessageBody().indexOf("\"", start);
                        if (start > 13 && end > start) {
                            requestId = msg.getMessageBody().substring(start, end);
                            int payloadStart = msg.getMessageBody().indexOf("\"payload\":", end);
                            if (payloadStart > 0) {
                                actualPayload = msg.getMessageBody().substring(payloadStart + 10);
                                if (actualPayload.endsWith("}")) {
                                    actualPayload = actualPayload.substring(0, actualPayload.length() - 1);
                                }
                            }
                            hasRequestId = true;
                        }
                    }

                    // 处理消息
                    BusinessProcessResult result = null;

                    try {
                        // 使用策略模式处理消息
                        BusinessProcessRequest request = 
                            new BusinessProcessRequest(requestId, actualPayload, queueName);
                        result = processorManager.processRequest(queueName, request);
                    } catch (Exception e) {
                        log.error("处理队列 {} 消息时发生错误: {}", queueName, e.getMessage(), e);
                        throw e;
                    } finally {
                        // 如果是同步消息，保存结果
                        if (hasRequestId && requestId != null && result != null) {
                            queueService.saveResult(
                                requestId, 
                                result.isSuccess(), 
                                result.getResultData() != null ? result.getResultData().toString() : null,
                                result.getErrorMessage());
                        }
                    }

                    // 确认消息处理完成
                    queueService.ack(msg.getId(), consumerId);
                    
                } else {
                    // 队列为空，休眠一段时间再尝试
                    Thread.sleep(queueService.getEmptyPollIntervalMs());
                }

                // 根据QPS配置控制消费速度
                long sleepMs = runtimeConfig.getSleepMsAfterProcess(queueName);
                if (sleepMs > 0) {
                    Thread.sleep(sleepMs);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("消费者线程被中断: {}", queueName);
                break;
            } catch (Exception e) {
                // 处理异常，可能是数据库连接问题或其他错误
                log.error("消费者循环处理异常 queue {}: {}", queueName, e.getMessage(), e);
                try {
                    // 发生错误时短暂休眠避免忙等待
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}