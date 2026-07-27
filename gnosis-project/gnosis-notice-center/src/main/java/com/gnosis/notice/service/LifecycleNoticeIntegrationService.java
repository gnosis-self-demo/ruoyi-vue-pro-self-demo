package com.gnosis.notice.service;

import com.gnosis.notice.dto.send.NoticeSendResponse;

import java.util.List;

/**
 * 生命周期通知集成服务接口
 * 用于在业务事件发生时发送通知
 */
public interface LifecycleNoticeIntegrationService {

    /**
     * 根据业务事件发送通知
     *
     * @param businessCode       业务编码
     * @param eventType          事件类型
     * @param businessId         业务ID
     * @param processInstanceId  流程实例ID
     * @return 发送响应列表
     */
    List<NoticeSendResponse> sendNoticeOnEvent(String businessCode, String eventType, String businessId, String processInstanceId);
}
