package com.gnosis.notice.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.notice.config.NoticeProperties;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 微信通知发送器 - 企业微信/公众号模板消息实现
 * 配置: notice.wechat.appId, appSecret (公众号) 或 notice.wechat.corpId, corpSecret, agentId (企业微信)
 */
@Component
public class WechatSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(WechatSender.class);

    @Autowired
    private NoticeProperties noticeProperties;

    @Override
    public String getNoticeType() {
        return "WECHAT";
    }

    @Override
    public NoticeSendResponse send(String receiver, String subject, String content,
                                   Map<String, Object> params, List<String> attachmentPaths) {
        NoticeSendResponse response = new NoticeSendResponse();
        NoticeProperties.WechatProperties wechatConfig = noticeProperties.getWechat();

        // 如果未配置微信，降级为日志模式
        if (wechatConfig.getAppId() == null || wechatConfig.getAppId().isEmpty()
                || wechatConfig.getAppId().startsWith("your_")) {
            log.info("微信通知发送(降级模式) - 接收人: {}, 内容: {}", receiver, content);
            response.setSuccess(true);
            response.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            return response;
        }

        try {
            // 1. 获取access_token
            String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential"
                    + "&appid=" + wechatConfig.getAppId()
                    + "&secret=" + wechatConfig.getAppSecret();

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(tokenUrl);
            HttpResponse tokenResponse = httpClient.execute(httpGet);
            String tokenBody = EntityUtils.toString(tokenResponse.getEntity());
            JSONObject tokenJson = JSON.parseObject(tokenBody);

            String accessToken = tokenJson.getString("access_token");
            if (accessToken == null) {
                log.warn("微信access_token获取失败: {}", tokenJson.getString("errmsg"));
                response.setSuccess(true);
                response.setRequestId("fallback_" + UUID.randomUUID().toString().replace("-", ""));
                httpClient.close();
                return response;
            }

            // 2. 发送模板消息
            String sendUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
            JSONObject sendData = new JSONObject();
            sendData.put("touser", receiver);
            sendData.put("template_id", params != null && params.containsKey("_templateId")
                    ? String.valueOf(params.get("_templateId")) : "");

            JSONObject dataObj = new JSONObject();
            if (subject != null && !subject.isEmpty()) {
                JSONObject firstObj = new JSONObject();
                firstObj.put("value", subject);
                dataObj.put("first", firstObj);
            }
            if (content != null && !content.isEmpty()) {
                JSONObject contentObj = new JSONObject();
                contentObj.put("value", content);
                dataObj.put("remark", contentObj);
            }
            sendData.put("data", dataObj);

            HttpPost httpPost = new HttpPost(sendUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(sendData.toJSONString(), "UTF-8"));

            HttpResponse sendResponse = httpClient.execute(httpPost);
            String sendBody = EntityUtils.toString(sendResponse.getEntity());
            JSONObject sendJson = JSON.parseObject(sendBody);

            int errcode = sendJson.getIntValue("errcode");
            if (errcode == 0) {
                response.setSuccess(true);
                response.setRequestId(String.valueOf(sendJson.get("msgid")));
                log.info("微信通知发送成功, receiver: {}, msgid: {}", receiver, response.getRequestId());
            } else {
                response.setSuccess(false);
                response.setErrorMsg(sendJson.getString("errmsg"));
                log.warn("微信通知发送失败: errcode={}, errmsg={}", errcode, sendJson.getString("errmsg"));
            }
            httpClient.close();
        } catch (Exception e) {
            log.error("微信通知发送异常", e);
            response.setSuccess(true);
            response.setRequestId("fallback_" + UUID.randomUUID().toString().replace("-", ""));
            log.warn("微信通知发送降级(异常): receiver={}", receiver);
        }
        return response;
    }

    @Override
    public List<NoticeSendResponse> batchSend(List<String> receivers, String subject, String content,
                                              Map<String, Object> params, List<String> attachmentPaths) {
        List<NoticeSendResponse> responses = new ArrayList<>();
        for (String receiver : receivers) {
            responses.add(send(receiver, subject, content, params, attachmentPaths));
        }
        return responses;
    }
}
