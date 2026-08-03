package com.gnosis.notice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消息通知上下文工具类
 * 用于获取当前操作用户ID
 */
@Component
public class NoticeContextUtil {

    private static final Logger log = LoggerFactory.getLogger(NoticeContextUtil.class);

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public void setCurrentUserId(String userId) {
        CURRENT_USER.set(userId);
    }

    /**
     * 获取当前用户ID
     * 优先从 ThreadLocal 获取，获取失败返回 "system"
     */
    public String getCurrentUserId() {
        try {
            String userId = CURRENT_USER.get();
            if (userId != null && !userId.isEmpty()) {
                return userId;
            }
        } catch (Exception e) {
            log.debug("获取当前登录用户失败，使用默认用户 system", e);
        }
        return "system";
    }

    /**
     * 清除当前用户ID
     */
    public void clear() {
        CURRENT_USER.remove();
    }
}
