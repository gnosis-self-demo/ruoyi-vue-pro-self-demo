package com.gnosis.notice.service.impl;

import com.gnosis.notice.config.NoticeProperties;
import com.gnosis.notice.service.NoticeFrequencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息发送频率控制服务实现
 * 使用本地ConcurrentHashMap + 滑动窗口计数
 */
@Service
public class NoticeFrequencyServiceImpl implements NoticeFrequencyService {

    private static final Logger log = LoggerFactory.getLogger(NoticeFrequencyServiceImpl.class);

    @Autowired
    private NoticeProperties noticeProperties;

    /**
     * 本地频率计数器 key -> 计数
     */
    private final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    /**
     * 本地频率计数器 key -> 窗口起始时间
     */
    private final Map<String, Long> windowStartMap = new ConcurrentHashMap<>();

    @Override
    public boolean checkFrequency(String receiver, String noticeType) {
        if (receiver == null || noticeType == null) {
            return true;
        }

        int maxPerMinute = noticeProperties.getFrequency() != null
                ? noticeProperties.getFrequency().getMaxPerMinute() : 10;

        String key = noticeType + ":" + receiver;
        long now = System.currentTimeMillis();
        Long windowStart = windowStartMap.get(key);

        // 如果窗口超过60秒，重置计数
        if (windowStart == null || (now - windowStart) > 60000) {
            windowStartMap.put(key, now);
            counterMap.put(key, new AtomicInteger(0));
            return true;
        }

        AtomicInteger counter = counterMap.get(key);
        return counter == null || counter.get() < maxPerMinute;
    }

    @Override
    public void incrementFrequency(String receiver, String noticeType) {
        if (receiver == null || noticeType == null) {
            return;
        }

        String key = noticeType + ":" + receiver;
        long now = System.currentTimeMillis();
        Long windowStart = windowStartMap.get(key);

        // 如果窗口超过60秒，重置计数
        if (windowStart == null || (now - windowStart) > 60000) {
            windowStartMap.put(key, now);
            counterMap.put(key, new AtomicInteger(0));
        }

        AtomicInteger counter = counterMap.computeIfAbsent(key, k -> new AtomicInteger(0));
        counter.incrementAndGet();

        // 定期清理过期的计数器（每5分钟清理一次）
        if (now - lastCleanTime > 300000) {
            synchronized (this) {
                if (now - lastCleanTime > 300000) {
                    cleanExpiredEntries(now);
                    lastCleanTime = now;
                }
            }
        }
    }

    private volatile long lastCleanTime = System.currentTimeMillis();

    /**
     * 清理超过5分钟的过期条目
     */
    private void cleanExpiredEntries(long now) {
        for (Map.Entry<String, Long> entry : windowStartMap.entrySet()) {
            if (now - entry.getValue() > 300000) {
                String key = entry.getKey();
                counterMap.remove(key);
                windowStartMap.remove(key);
            }
        }
    }
}
