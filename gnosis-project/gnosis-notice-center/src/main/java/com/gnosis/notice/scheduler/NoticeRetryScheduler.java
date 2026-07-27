package com.gnosis.notice.scheduler;

import com.gnosis.notice.config.NoticeProperties;
import com.gnosis.notice.domain.NoticeLog;
import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.mapper.NoticeLogMapper;
import com.gnosis.notice.service.NoticeSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 消息发送重试调度器
 * 按配置间隔扫描失败日志并重新发送
 */
@Component
public class NoticeRetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(NoticeRetryScheduler.class);

    @Autowired
    private NoticeLogMapper logMapper;

    @Autowired
    private NoticeSendService sendService;

    @Autowired
    private NoticeProperties noticeProperties;

    /**
     * 按配置间隔执行重试
     */
    @Scheduled(fixedDelayString = "${notice.retry.interval:60000}")
    public void execute() {
        log.info("消息重试调度器开始执行");

        Integer maxCount = noticeProperties.getRetry().getMaxCount();
        List<NoticeLog> retryList;
        try {
            retryList = logMapper.selectRetryList(maxCount);
        } catch (Exception e) {
            log.error("查询重试列表失败", e);
            return;
        }

        if (retryList == null || retryList.isEmpty()) {
            log.debug("无需要重试的日志");
            return;
        }

        log.info("发现 {} 条需要重试的日志", retryList.size());

        for (NoticeLog logRecord : retryList) {
            try {
                // 1. 递增重试次数
                int newRetryCount = (logRecord.getRetryCount() != null ? logRecord.getRetryCount() : 0) + 1;
                logRecord.setRetryCount(newRetryCount);
                logRecord.setSendStatus(0); // 重置为待发送
                logRecord.setUpdateTime(new Date());
                logMapper.updateById(logRecord);

                // 2. 构建发送请求
                NoticeSendRequest request = new NoticeSendRequest();
                request.setTemplateCode(logRecord.getTemplateCode());
                request.setNoticeType(logRecord.getNoticeType());
                request.setReceiver(logRecord.getReceiver());
                request.setSubject(logRecord.getSubject());
                request.setContent(logRecord.getContent());

                // 3. 重新发送
                NoticeSendResponse response = sendService.send(request, "system");

                // 4. 更新日志状态
                if (response.getSuccess()) {
                    logRecord.setSendStatus(2); // 成功
                    logRecord.setSendTime(new Date());
                    logRecord.setRequestId(response.getRequestId());
                } else {
                    logRecord.setSendStatus(3); // 失败
                    logRecord.setErrorMsg(response.getErrorMsg());
                }
                logRecord.setUpdateTime(new Date());
                logMapper.updateById(logRecord);

                log.info("重试完成, logId={}, success={}, retryCount={}",
                        logRecord.getId(), response.getSuccess(), newRetryCount);
            } catch (Exception e) {
                log.error("重试失败, logId={}", logRecord.getId(), e);
                try {
                    logRecord.setSendStatus(3);
                    logRecord.setErrorMsg(e.getMessage());
                    logRecord.setUpdateTime(new Date());
                    logMapper.updateById(logRecord);
                } catch (Exception ex) {
                    log.error("更新重试日志状态失败, logId={}", logRecord.getId(), ex);
                }
            }
        }

        log.info("消息重试调度器执行完成");
    }
}
