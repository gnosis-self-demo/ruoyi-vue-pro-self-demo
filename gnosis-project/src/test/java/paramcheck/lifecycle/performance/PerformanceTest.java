package paramcheck.lifecycle.performance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import lifecycle.dto.EventSubmitRequest;
import lifecycle.dto.EventVO;
import lifecycle.service.EventService;
import lifecycle.service.EventServiceImpl;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 性能测试类
 * 包含并发测试、负载测试和压力测试
 */
@RunWith(MockitoJUnitRunner.class)
public class PerformanceTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventServiceImpl eventServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        // 配置 Mock 行为 - 模拟事件提交延迟
        when(eventService.submitEvent(any(EventSubmitRequest.class)))
                .thenAnswer(invocation -> {
                    // 模拟 10-50ms 的处理延迟
                    try {
                        Thread.sleep(10 + new Random().nextInt(40));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    EventVO response = new EventVO();
                    response.setId("event_" + System.currentTimeMillis());
                    response.setBusinessTypeId("biz_type_perf");
                    response.setEventType("PERF_TEST");
                    return response;
                });
    }

    /**
     * 测试 100 并发提交事件
     */
    @Test
    public void testConcurrent100SubmitEvents() throws InterruptedException {
        System.out.println("=== 开始 100 并发提交事件测试 ===");
        long startTime = System.currentTimeMillis();

        final int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Long> responseTimes = new ConcurrentLinkedQueue<>();

        // 提交 100 个并发任务
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    
                    EventSubmitRequest request = new EventSubmitRequest();
                    request.setBusinessTypeId("biz_type_perf");
                    request.setEventType("CONCURRENT_TEST");
                    request.setBusinessInstanceId("instance_" + index + "_" + System.currentTimeMillis());
                    request.setOperatorId("perf_user");
                    request.setEventData(new HashMap<>());
                    request.setMetadata(new HashMap<>());

                    EventVO result = eventService.submitEvent(request);
                    
                    long responseTime = System.currentTimeMillis() - requestStart;
                    responseTimes.offer(responseTime);
                    
                    if (result != null) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成
        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 计算统计信息
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        long maxResponseTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long minResponseTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);

        // 输出结果
        System.out.println("总耗时：" + totalTime + "ms");
        System.out.println("成功数量：" + successCount.get());
        System.out.println("失败数量：" + failCount.get());
        System.out.println("平均响应时间：" + avgResponseTime + "ms");
        System.out.println("最大响应时间：" + maxResponseTime + "ms");
        System.out.println("最小响应时间：" + minResponseTime + "ms");
        System.out.println("吞吐量：" + (successCount.get() * 1000.0 / totalTime) + " requests/s");

        // 验证结果
        assertTrue("成功率应该大于 90%", successCount.get() >= threadCount * 0.9);
        assertTrue("平均响应时间应该小于 500ms", avgResponseTime < 500);
    }

    /**
     * 测试 50 并发状态流转
     */
    @Test
    public void testConcurrent50StateTransitions() throws InterruptedException {
        System.out.println("\n=== 开始 50 并发状态流转测试 ===");
        long startTime = System.currentTimeMillis();

        final int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 提交 50 个并发任务
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    // 模拟状态流转处理
                    Thread.sleep(20 + new Random().nextInt(30));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        long totalTime = System.currentTimeMillis() - startTime;

        System.out.println("总耗时：" + totalTime + "ms");
        System.out.println("成功数量：" + successCount.get());
        System.out.println("失败数量：" + failCount.get());
        System.out.println("吞吐量：" + (successCount.get() * 1000.0 / totalTime) + " transitions/s");

        // 验证结果
        assertTrue("成功率应该大于 95%", successCount.get() >= threadCount * 0.95);
    }

    /**
     * 测试 200 并发查询
     */
    @Test
    public void testConcurrent200Queries() throws InterruptedException {
        System.out.println("\n=== 开始 200 并发查询测试 ===");
        long startTime = System.currentTimeMillis();

        final int threadCount = 200;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Long> responseTimes = new ConcurrentLinkedQueue<>();

        // 配置 Mock 行为 - 模拟查询延迟
        when(eventService.list(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    Thread.sleep(5 + new Random().nextInt(20));
                    return new ArrayList<>();
                });

        // 提交 200 个并发查询任务
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    
                    eventService.list("biz_type_perf", null, 1, 10);
                    
                    long responseTime = System.currentTimeMillis() - requestStart;
                    responseTimes.offer(responseTime);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        long totalTime = System.currentTimeMillis() - startTime;
        double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);

        System.out.println("总耗时：" + totalTime + "ms");
        System.out.println("成功数量：" + successCount.get());
        System.out.println("平均响应时间：" + avgResponseTime + "ms");
        System.out.println("吞吐量：" + (successCount.get() * 1000.0 / totalTime) + " queries/s");

        // 验证结果
        assertTrue("成功率应该大于 98%", successCount.get() >= threadCount * 0.98);
        assertTrue("平均响应时间应该小于 100ms", avgResponseTime < 100);
    }

    /**
     * 持续 1 分钟的事件提交负载测试
     */
    @Test
    public void testOneMinuteEventSubmitLoad() throws InterruptedException {
        System.out.println("\n=== 开始 1 分钟事件提交负载测试 ===");
        
        final int durationSeconds = 60;
        final int targetRPS = 10; // 目标每秒 10 个请求
        
        ExecutorService executor = Executors.newFixedThreadPool(20);
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successRequests = new AtomicInteger(0);
        AtomicInteger failRequests = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(1);
        
        // 启动持续发送请求的任务
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - startTime >= durationSeconds * 1000) {
                latch.countDown();
                return;
            }
            
            for (int i = 0; i < targetRPS; i++) {
                executor.submit(() -> {
                    try {
                        EventSubmitRequest request = new EventSubmitRequest();
                        request.setBusinessTypeId("biz_type_load");
                        request.setEventType("LOAD_TEST");
                        request.setBusinessInstanceId("load_" + System.currentTimeMillis());
                        request.setOperatorId("load_user");
                        request.setEventData(new HashMap<>());
                        request.setMetadata(new HashMap<>());
                        
                        EventVO result = eventService.submitEvent(request);
                        totalRequests.incrementAndGet();
                        
                        if (result != null) {
                            successRequests.incrementAndGet();
                        } else {
                            failRequests.incrementAndGet();
                        }
                    } catch (Exception e) {
                        totalRequests.incrementAndGet();
                        failRequests.incrementAndGet();
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // 等待测试完成
        latch.await();
        scheduler.shutdown();
        executor.shutdown();
        
        // 等待所有任务完成
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        long totalTime = System.currentTimeMillis() - startTime;
        double actualRPS = successRequests.get() * 1000.0 / totalTime;
        
        System.out.println("测试持续时间：" + totalTime + "ms");
        System.out.println("总请求数：" + totalRequests.get());
        System.out.println("成功请求数：" + successRequests.get());
        System.out.println("失败请求数：" + failRequests.get());
        System.out.println("实际 RPS: " + actualRPS);
        System.out.println("成功率：" + (successRequests.get() * 100.0 / totalRequests.get()) + "%");
        
        // 验证结果
        assertTrue("成功率应该大于 95%", successRequests.get() * 100.0 / totalRequests.get() >= 95);
    }

    /**
     * 持续 5 分钟的查询负载测试
     */
    @Test
    public void testFiveMinuteQueryLoad() throws InterruptedException {
        System.out.println("\n=== 开始 5 分钟查询负载测试 ===");
        
        final int durationSeconds = 300; // 5 分钟
        final int targetRPS = 20; // 目标每秒 20 个查询
        
        ExecutorService executor = Executors.newFixedThreadPool(30);
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successRequests = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(1);
        
        // 配置 Mock 行为
        when(eventService.list(anyString(), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    Thread.sleep(2 + new Random().nextInt(10));
                    return new ArrayList<>();
                });
        
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - startTime >= durationSeconds * 1000) {
                latch.countDown();
                return;
            }
            
            for (int i = 0; i < targetRPS; i++) {
                executor.submit(() -> {
                    try {
                        eventService.list("biz_type_load", null, 1, 10);
                        totalRequests.incrementAndGet();
                        successRequests.incrementAndGet();
                    } catch (Exception e) {
                        totalRequests.incrementAndGet();
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // 等待测试完成
        latch.await();
        scheduler.shutdown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        long totalTime = System.currentTimeMillis() - startTime;
        double actualRPS = successRequests.get() * 1000.0 / totalTime;
        
        System.out.println("测试持续时间：" + totalTime + "ms");
        System.out.println("总查询数：" + totalRequests.get());
        System.out.println("成功查询数：" + successRequests.get());
        System.out.println("实际 RPS: " + actualRPS);
        
        // 验证结果
        assertTrue("查询应该全部成功", successRequests.get() == totalRequests.get());
    }

    /**
     * 压力测试 - 逐步增加并发直到系统瓶颈
     */
    @Test
    public void testStressTestIncreasingLoad() throws InterruptedException {
        System.out.println("\n=== 开始压力测试 - 逐步增加并发 ===");
        
        int[] concurrencyLevels = {10, 20, 50, 100, 200, 500};
        
        for (int concurrency : concurrencyLevels) {
            System.out.println("\n--- 并发级别：" + concurrency + " ---");
            
            ExecutorService executor = Executors.newFixedThreadPool(concurrency);
            CountDownLatch latch = new CountDownLatch(concurrency);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            ConcurrentLinkedQueue<Long> responseTimes = new ConcurrentLinkedQueue<>();
            
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < concurrency; i++) {
                final int index = i;
                executor.submit(() -> {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        EventSubmitRequest request = new EventSubmitRequest();
                        request.setBusinessTypeId("biz_type_stress");
                        request.setEventType("STRESS_TEST");
                        request.setBusinessInstanceId("stress_" + index);
                        request.setOperatorId("stress_user");
                        request.setEventData(new HashMap<>());
                        request.setMetadata(new HashMap<>());
                        
                        EventVO result = eventService.submitEvent(request);
                        
                        long responseTime = System.currentTimeMillis() - requestStart;
                        responseTimes.offer(responseTime);
                        
                        if (result != null) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            latch.await(60, TimeUnit.SECONDS);
            executor.shutdown();
            
            long totalTime = System.currentTimeMillis() - startTime;
            double avgResponseTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            double throughput = successCount.get() * 1000.0 / totalTime;
            
            System.out.println("成功：" + successCount.get() + ", 失败：" + failCount.get());
            System.out.println("平均响应时间：" + avgResponseTime + "ms");
            System.out.println("吞吐量：" + throughput + " requests/s");
            
            // 如果失败率超过 10%，认为达到瓶颈
            if (failCount.get() > concurrency * 0.1) {
                System.out.println("!!! 达到系统瓶颈 - 并发级别：" + concurrency);
                break;
            }
        }
    }
}
