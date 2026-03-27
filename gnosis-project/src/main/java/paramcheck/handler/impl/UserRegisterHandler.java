package paramcheck.handler.impl;

import paramcheck.domain.ValidationContext;
import paramcheck.domain.ValidationResult;
import paramcheck.handler.IValidationHandler;
import paramcheck.handler.ValidationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 用户注册 Handler (HANDLER 纯接口模式示例)
 * 负责复杂的密码强度校验、用户名重复检查等
 */
@Component
@ValidationHandler("UserRegisterHandler")
public class UserRegisterHandler implements IValidationHandler {

    private static final Logger log = LoggerFactory.getLogger(UserRegisterHandler.class);

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w.-]+@[\\w.-]+\\.\\w+$");

    @Autowired
    @Qualifier("gnosisJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public ValidationResult validate(ValidationContext context) {
        Object rawData = context.getData("rawRequest");
        log.info("[UserRegisterHandler] start validation, requestId={}", context.getRequestId());

        // 1) 用户名非空
        String username = extract(rawData, "$.username");
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.fail("USERNAME_EMPTY", "用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 32) {
            return ValidationResult.fail("USERNAME_LENGTH", "用户名长度需在 3~32 位之间");
        }

        // 2) 用户名重复检查
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE username = ?",
                Integer.class, username);
        if (count != null && count > 0) {
            return ValidationResult.fail("USERNAME_EXISTS", "用户名已存在: " + username);
        }

        // 3) 密码强度校验
        String password = extract(rawData, "$.password");
        if (password == null || password.trim().isEmpty()) {
            return ValidationResult.fail("PASSWORD_EMPTY", "密码不能为空");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ValidationResult.fail("PASSWORD_WEAK",
                    "密码强度不足：至少8位，需包含大小写字母和数字");
        }

        // 4) 邮箱格式校验
        String email = extract(rawData, "$.email");
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return ValidationResult.fail("EMAIL_FORMAT", "邮箱格式不正确: " + email);
            }
            Integer emailCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE email = ?",
                    Integer.class, email);
            if (emailCount != null && emailCount > 0) {
                return ValidationResult.fail("EMAIL_EXISTS", "邮箱已被注册: " + email);
            }
        }

        log.info("[UserRegisterHandler] validation passed, username={}", username);
        return ValidationResult.success();
    }

    private String extract(Object data, String path) {
        if (data == null) return null;
        try {
            return String.valueOf(com.jayway.jsonpath.JsonPath.read(data, path));
        } catch (Exception e) {
            return null;
        }
    }
}
