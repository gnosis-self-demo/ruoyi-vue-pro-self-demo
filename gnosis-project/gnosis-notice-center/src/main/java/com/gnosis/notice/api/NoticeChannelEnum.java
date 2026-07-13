package com.gnosis.notice.api;

/**
 * 消息通道枚举
 */
public enum NoticeChannelEnum {

    /** 短信 */
    SMS("SMS", "短信"),

    /** 邮件 */
    EMAIL("EMAIL", "邮件"),

    /** 站内信 */
    INBOX("INBOX", "站内信"),

    /** 微信 */
    WECHAT("WECHAT", "微信通知");

    private final String code;
    private final String name;

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
        for (NoticeChannelEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
