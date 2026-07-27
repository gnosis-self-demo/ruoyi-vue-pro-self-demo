package com.gnosis.notice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 消息通知配置属性
 */
@Component
@ConfigurationProperties(prefix = "notice")
public class NoticeProperties {

    /**
     * 短信配置
     */
    private SmsProperties sms = new SmsProperties();

    /**
     * 邮件配置
     */
    private EmailProperties email = new EmailProperties();

    /**
     * 微信配置
     */
    private WechatProperties wechat = new WechatProperties();

    /**
     * 钉钉配置
     */
    private DingTalkProperties dingtalk = new DingTalkProperties();

    /**
     * 频率控制配置
     */
    private FrequencyProperties frequency = new FrequencyProperties();

    /**
     * 重试配置
     */
    private RetryProperties retry = new RetryProperties();

    public SmsProperties getSms() {
        return sms;
    }

    public void setSms(SmsProperties sms) {
        this.sms = sms;
    }

    public EmailProperties getEmail() {
        return email;
    }

    public void setEmail(EmailProperties email) {
        this.email = email;
    }

    public WechatProperties getWechat() {
        return wechat;
    }

    public void setWechat(WechatProperties wechat) {
        this.wechat = wechat;
    }

    public DingTalkProperties getDingtalk() {
        return dingtalk;
    }

    public void setDingtalk(DingTalkProperties dingtalk) {
        this.dingtalk = dingtalk;
    }

    public FrequencyProperties getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyProperties frequency) {
        this.frequency = frequency;
    }

    public RetryProperties getRetry() {
        return retry;
    }

    public void setRetry(RetryProperties retry) {
        this.retry = retry;
    }

    /**
     * 短信配置
     */
    public static class SmsProperties {
        private String provider;
        private String accessKeyId;
        private String accessKeySecret;
        private String signName;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getSignName() {
            return signName;
        }

        public void setSignName(String signName) {
            this.signName = signName;
        }
    }

    /**
     * 邮件配置
     */
    public static class EmailProperties {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private Boolean sslEnable;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Boolean getSslEnable() {
            return sslEnable;
        }

        public void setSslEnable(Boolean sslEnable) {
            this.sslEnable = sslEnable;
        }
    }

    /**
     * 微信配置
     */
    public static class WechatProperties {
        private String appId;
        private String appSecret;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }
    }

    /**
     * 重试配置
     */
    public static class RetryProperties {
        private Integer maxCount = 3;
        private Long interval = 1000L;

        public Integer getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(Integer maxCount) {
            this.maxCount = maxCount;
        }

        public Long getInterval() {
            return interval;
        }

        public void setInterval(Long interval) {
            this.interval = interval;
        }
    }

    /**
     * 钉钉配置
     */
    public static class DingTalkProperties {
        private String appKey;
        private String appSecret;
        private Long agentId;

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public Long getAgentId() {
            return agentId;
        }

        public void setAgentId(Long agentId) {
            this.agentId = agentId;
        }
    }

    /**
     * 频率控制配置
     */
    public static class FrequencyProperties {
        private Integer maxPerMinute = 10;

        public Integer getMaxPerMinute() {
            return maxPerMinute;
        }

        public void setMaxPerMinute(Integer maxPerMinute) {
            this.maxPerMinute = maxPerMinute;
        }
    }
}
