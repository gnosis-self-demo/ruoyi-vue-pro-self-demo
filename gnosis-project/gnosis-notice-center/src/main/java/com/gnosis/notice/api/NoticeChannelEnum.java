package com.gnosis.notice.api;

/**
 * 消息通道枚举 - 扩展版: 增加DINGTALK
 */
public enum NoticeChannelEnum {

    SMS("SMS", "短信"),
    EMAIL("EMAIL", "邮件"),
    INBOX("INBOX", "站内信"),
    WECHAT("WECHAT", "微信通知"),
    DINGTALK("DINGTALK", "钉钉通知");

    private String code;
    private String name;

    NoticeChannelEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static NoticeChannelEnum getByCode(String code) {
        for (NoticeChannelEnum channel : NoticeChannelEnum.values()) {
            if (channel.getCode().equals(code)) {
                return channel;
            }
        }
        return null;
    }
}
