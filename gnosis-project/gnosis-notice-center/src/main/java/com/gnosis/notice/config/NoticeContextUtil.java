package com.gnosis.notice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 消息通知上下文工具类
 * 用于获取当前操作用户ID，优先从Spring Security获取，获取失败返回默认用户
 */
@Component
public class NoticeContextUtil {

    private static final Logger log = LoggerFactory.getLogger(NoticeContextUtil.class);

    /**
     * 获取当前用户ID
     * 优先从 SecurityContextHolder 获取，获取失败返回 "system"
     */
    public String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("获取当前登录用户失败，使用默认用户 system", e);
        }
        return "system";
    }
}
