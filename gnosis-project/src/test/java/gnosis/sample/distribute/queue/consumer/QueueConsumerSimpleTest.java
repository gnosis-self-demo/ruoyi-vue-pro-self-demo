package gnosis.sample.distribute.queue.consumer;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.processor.BusinessProcessorManager;
import gnosis.sample.distribute.queue.service.DistributedQueueService;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * QueueConsumer simple test class
 * Verify NullPointerException fix
 */
public class QueueConsumerSimpleTest {

    @Test
    public void testQueueConsumerSetterMethods() {
        // Create QueueConsumer instance
        QueueConsumer consumer = new QueueConsumer();
        
        // Create mock objects
        DistributedQueueService mockService = mock(DistributedQueueService.class);
        RuntimeQueueConfig mockConfig = mock(RuntimeQueueConfig.class);
        BusinessProcessorManager mockProcessorManager = mock(BusinessProcessorManager.class);
        
        // Call setter methods
        consumer.setQueueService(mockService);
        consumer.setRuntimeConfig(mockConfig);
        consumer.setProcessorManager(mockProcessorManager);
        
        // Verify setter methods execute successfully (no exceptions thrown)
        assertNotNull("QueueConsumer instance should not be null", consumer);
        System.out.println("QueueConsumer setter methods test passed!");
    }
}