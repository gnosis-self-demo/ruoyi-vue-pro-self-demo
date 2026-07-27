package com.gnosis.notice.service.impl;

import com.gnosis.notice.api.NoticeApi;
import com.gnosis.notice.api.NoticeChannelEnum;
import com.gnosis.notice.domain.NoticeTemplate;
import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.service.LifecycleNoticeIntegrationService;
import com.gnosis.notice.service.NoticeTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生命周期通知集成服务实现
 */
@Service
public class LifecycleNoticeIntegrationServiceImpl implements LifecycleNoticeIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(LifecycleNoticeIntegrationServiceImpl.class);

    private static final String TEMPLATE_CODE_PREFIX = "NOTICE_";

    @Autowired
    private NoticeApi noticeApi;

    @Autowired
    private NoticeTemplateService templateService;

    @Override
    public List<NoticeSendResponse> sendNoticeOnEvent(String businessCode, String eventType,
                                                      String businessId, String processInstanceId) {
        List<NoticeSendResponse> responses = new ArrayList<NoticeSendResponse>();

        if (businessCode == null || eventType == null) {
            log.warn("业务编码或事件类型为空，跳过通知发送");
            return responses;
        }

        // 1. 根据businessCode查找关联的通知模板
        // 模板编码约定: "NOTICE_{businessCode}_{eventType}"
        String templateCode = TEMPLATE_CODE_PREFIX + businessCode + "_" + eventType;
        NoticeTemplate template = templateService.getByCode(templateCode);

        if (template == null) {
            log.info("未找到通知模板, templateCode={}, 跳过通知发送", templateCode);
            return responses;
        }

        try {
            // 2. 构建发送请求
            NoticeSendRequest request = new NoticeSendRequest();
            request.setTemplateCode(templateCode);

            // 3. 根据事件类型确定通知通道和接收人规则
            NoticeChannelEnum channel = determineChannel(template.getNoticeType(), eventType);
            if (channel == null) {
                log.warn("无法确定通知通道, templateCode={}, eventType={}", templateCode, eventType);
                return responses;
            }

            // 4. 设置参数
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("businessCode", businessCode);
            params.put("eventType", eventType);
            params.put("businessId", businessId);
            params.put("processInstanceId", processInstanceId);
            request.setParams(params);

            // 5. 发送通知
            NoticeSendResponse response = noticeApi.send(channel, request, "system");
            responses.add(response);

            log.info("生命周期通知发送完成, templateCode={}, success={}", templateCode, response.getSuccess());
        } catch (Exception e) {
            log.error("生命周期通知发送异常, templateCode={}", templateCode, e);
        }

        return responses;
    }

    /**
     * 根据通知类型和事件类型确定通知通道
     */
    private NoticeChannelEnum determineChannel(String noticeType, String eventType) {
        if (noticeType == null) {
            // 根据事件类型推断默认通道
            if ("APPROVAL".equalsIgnoreCase(eventType) || "TODO".equalsIgnoreCase(eventType)) {
                return NoticeChannelEnum.INBOX;
            }
            return null;
        }

        try {
            return NoticeChannelEnum.valueOf(noticeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("不支持的通知通道: {}", noticeType);
            return null;
        }
    }
}
