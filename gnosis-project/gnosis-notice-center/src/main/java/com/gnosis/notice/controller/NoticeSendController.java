package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.service.NoticeSendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息发送接口（对外API）
 */
@Api(tags = "消息发送")
@RestController
@RequestMapping("/send")
public class NoticeSendController {

    @Autowired
    private NoticeSendService sendService;

    @ApiOperation("发送消息（使用模板）")
    @PostMapping("/send")
    public BaseResponse<NoticeSendResponse> send(@RequestBody NoticeSendRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        NoticeSendResponse response = sendService.send(request, userId);
        return ResponseUtil.ok(response);
    }

    @ApiOperation("批量发送消息")
    @PostMapping("/batch")
    public BaseResponse<List<NoticeSendResponse>> batchSend(@RequestBody NoticeSendRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        List<NoticeSendResponse> responses = sendService.batchSend(request, userId);
        return ResponseUtil.ok(responses);
    }

    @ApiOperation("快速发送短信")
    @PostMapping("/sms")
    public BaseResponse<NoticeSendResponse> sendSms(@RequestBody NoticeSendRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        NoticeSendResponse response = sendService.sendSms(request.getReceiver(), request.getContent(), userId);
        return ResponseUtil.ok(response);
    }

    @ApiOperation("快速发送邮件")
    @PostMapping("/email")
    public BaseResponse<NoticeSendResponse> sendEmail(@RequestBody NoticeSendRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        NoticeSendResponse response = sendService.sendEmail(request.getReceiver(), request.getSubject(), request.getContent(), userId);
        return ResponseUtil.ok(response);
    }

    @ApiOperation("发送站内信")
    @PostMapping("/inbox")
    public BaseResponse<List<NoticeSendResponse>> sendInbox(@RequestBody NoticeSendRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        List<NoticeSendResponse> responses = sendService.sendInbox(request.getReceivers(), request.getSubject(), request.getContent(), userId);
        return ResponseUtil.ok(responses);
    }

    @ApiOperation("发送微信通知")
    @PostMapping("/wechat")
    public BaseResponse<NoticeSendResponse> sendWechat(@RequestBody NoticeSendRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        NoticeSendResponse response = sendService.sendWechat(request.getReceiver(), request.getContent(), userId);
        return ResponseUtil.ok(response);
    }
}
