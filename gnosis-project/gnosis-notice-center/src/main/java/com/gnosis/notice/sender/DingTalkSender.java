package com.gnosis.notice.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.notice.config.NoticeProperties;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 钉钉工作通知发送器
 */
@Component
public class DingTalkSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(DingTalkSender.class);

    @Autowired
    private NoticeProperties noticeProperties;

    @Override
    public String getNoticeType() {
        return "DINGTALK";
    }

    @Override
    public NoticeSendResponse send(String receiver, String subject, String content,
                                   Map<String, Object> params, List<String> attachmentPaths) {
        NoticeSendResponse response = new NoticeSendResponse();
        response.setRequestId(UUID.randomUUID().toString().replace("-", ""));

        try {
            NoticeProperties.DingTalkProperties dingtalk = noticeProperties.getDingtalk();
            if (dingtalk == null || dingtalk.getAppKey() == null || dingtalk.getAppSecret() == null) {
                log.warn("钉钉配置未设置, 使用桩实现");
                response.setSuccess(true);
                response.setRequestId(UUID.randomUUID().toString().replace("-", ""));
                return response;
            }

            // 1. 获取access_token
            String accessToken = getAccessToken(dingtalk.getAppKey(), dingtalk.getAppSecret());
            if (accessToken == null) {
                response.setSuccess(false);
                response.setErrorMsg("获取钉钉access_token失败");
                return response;
            }

            // 2. 发送工作通知
            JSONObject sendResult = sendWorkNotice(accessToken, dingtalk.getAgentId(), receiver, content);
            if (sendResult != null) {
                Integer errcode = sendResult.getInteger("errcode");
                if (errcode != null && errcode == 0) {
                    response.setSuccess(true);
                    log.info("钉钉消息发送成功, receiver={}", receiver);
                } else {
                    response.setSuccess(false);
                    response.setErrorMsg(sendResult.getString("errmsg"));
                    log.error("钉钉消息发送失败, errcode={}, errmsg={}", errcode, sendResult.getString("errmsg"));
                }
            } else {
                response.setSuccess(false);
                response.setErrorMsg("钉钉接口返回为空");
            }
        } catch (Exception e) {
            log.error("钉钉消息发送异常", e);
            response.setSuccess(false);
            response.setErrorMsg(e.getMessage());
        }

        return response;
    }

    @Override
    public List<NoticeSendResponse> batchSend(List<String> receivers, String subject, String content,
                                              Map<String, Object> params, List<String> attachmentPaths) {
        List<NoticeSendResponse> responses = new ArrayList<NoticeSendResponse>();
        if (receivers != null) {
            for (String receiver : receivers) {
                responses.add(send(receiver, subject, content, params, attachmentPaths));
            }
        }
        return responses;
    }

    /**
     * 获取钉钉access_token
     */
    private String getAccessToken(String appKey, String appSecret) {
        try {
            String url = "https://oapi.dingtalk.com/gettoken?appkey=" + appKey + "&appsecret=" + appSecret;
            String result = httpGet(url);
            if (result != null) {
                JSONObject json = JSON.parseObject(result);
                Integer errcode = json.getInteger("errcode");
                if (errcode != null && errcode == 0) {
                    return json.getString("access_token");
                } else {
                    log.error("获取钉钉access_token失败: {}", result);
                }
            }
        } catch (Exception e) {
            log.error("获取钉钉access_token异常", e);
        }
        return null;
    }

    /**
     * 发送工作通知
     */
    private JSONObject sendWorkNotice(String accessToken, Long agentId, String userId, String content) {
        try {
            String url = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=" + accessToken;

            JSONObject msg = new JSONObject();
            msg.put("msgtype", "text");
            JSONObject text = new JSONObject();
            text.put("content", content);
            msg.put("text", text);

            JSONObject body = new JSONObject();
            body.put("agent_id", agentId);
            body.put("userid_list", userId);
            body.put("msg", msg);

            String result = httpPost(url, body.toJSONString());
            if (result != null) {
                return JSON.parseObject(result);
            }
        } catch (Exception e) {
            log.error("发送钉钉工作通知异常", e);
        }
        return null;
    }

    /**
     * HTTP GET 请求
     */
    private String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        }
        return null;
    }

    /**
     * HTTP POST 请求
     */
    private String httpPost(String urlStr, String body) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        OutputStream os = conn.getOutputStream();
        os.write(body.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        }
        return null;
    }
}
