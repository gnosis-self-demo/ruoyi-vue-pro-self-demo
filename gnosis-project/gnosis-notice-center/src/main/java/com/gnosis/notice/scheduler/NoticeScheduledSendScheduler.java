package com.gnosis.notice.scheduler;

import com.gnosis.notice.domain.NoticeScheduledSend;
import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.mapper.NoticeScheduledSendMapper;
import com.gnosis.notice.service.NoticeSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 定时发送调度器
 * 每分钟扫描待发送列表并执行发送
 */
@Component
public class NoticeScheduledSendScheduler {

    private static final Logger log = LoggerFactory.getLogger(NoticeScheduledSendScheduler.class);

    @Autowired
    private NoticeScheduledSendMapper scheduledSendMapper;

    @Autowired
    private NoticeSendService sendService;

    /**
     * 每分钟执行一次，查询待发送列表并发送
     */
    @Scheduled(fixedDelay = 60000)
    public void execute() {
        log.debug("定时发送调度器开始执行");
        List<NoticeScheduledSend> pendingList;
        try {
            pendingList = scheduledSendMapper.selectPendingList();
        } catch (Exception e) {
            log.error("查询待发送列表失败", e);
            return;
        }

        if (pendingList == null || pendingList.isEmpty()) {
            log.debug("无待发送的定时任务");
            return;
        }

        log.info("发现 {} 条待发送定时任务", pendingList.size());

        for (NoticeScheduledSend scheduledSend : pendingList) {
            try {
                // 1. 构建发送请求
                NoticeSendRequest request = new NoticeSendRequest();
                request.setTemplateCode(scheduledSend.getTemplateCode());
                request.setNoticeType(scheduledSend.getNoticeType());
                request.setReceiver(scheduledSend.getReceiver());
                request.setSubject(scheduledSend.getSubject());
                request.setContent(scheduledSend.getContent());

                // 2. 调用发送服务
                NoticeSendResponse response = sendService.send(request, "system");

                // 3. 更新定时任务状态
                if (response.getSuccess()) {
                    scheduledSend.setSendStatus(1); // 已发送
                    scheduledSend.setLogId(response.getLogId());
                } else {
                    scheduledSend.setSendStatus(2); // 失败
                    scheduledSend.setErrorMsg(response.getErrorMsg());
                }
                scheduledSend.setUpdateTime(new Date());
                scheduledSendMapper.updateById(scheduledSend);

                log.info("定时任务执行完成, id={}, success={}", scheduledSend.getId(), response.getSuccess());
            } catch (Exception e) {
                log.error("定时任务执行失败, id={}", scheduledSend.getId(), e);
                // 更新任务状态为失败
                scheduledSend.setSendStatus(2);
                scheduledSend.setErrorMsg(e.getMessage());
                scheduledSend.setUpdateTime(new Date());
                try {
                    scheduledSendMapper.updateById(scheduledSend);
                } catch (Exception ex) {
                    log.error("更新定时任务状态失败, id={}", scheduledSend.getId(), ex);
                }
            }
        }
    }
}
