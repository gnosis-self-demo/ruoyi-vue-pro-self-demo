package lifecycle.entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lifecycle.domain.LifecycleBusinessType;
import lifecycle.dto.EntryValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限检查器
 * 负责检查用户对业务入口的访问权限
 */
@Component
public class PermissionChecker {

    private static final Logger log = LoggerFactory.getLogger(PermissionChecker.class);

    /**
     * 检查权限
     *
     * @param userId 用户 ID
     * @param userRoles 用户角色列表
     * @param businessType 业务类型
     * @return 校验结果
     */
    public EntryValidationResult checkPermission(String userId, List<String> userRoles,
                                                 LifecycleBusinessType businessType) {
        log.debug("[PermissionChecker] 检查权限：userId={}, businessType={}", 
                userId, businessType.getCode());

        // 获取权限配置
        String permissionConfig = businessType.getEntryPermissionConfig();
        
        // 如果没有配置权限，默认允许访问
        if (permissionConfig == null || permissionConfig.trim().isEmpty()) {
            log.debug("[PermissionChecker] 未配置权限，允许访问：businessType={}", businessType.getCode());
            return EntryValidationResult.success("未配置权限限制，允许访问");
        }

        try {
            JSONObject config = JSON.parseObject(permissionConfig);
            
            // 检查是否需要特定角色
            if (config.containsKey("roles")) {
                JSONArray rolesArray = config.getJSONArray("roles");
                if (rolesArray != null && !rolesArray.isEmpty()) {
                    List<String> requiredRoles = rolesArray.toJavaList(String.class);
                    
                    // 如果用户角色为空，拒绝访问
                    if (userRoles == null || userRoles.isEmpty()) {
                        return EntryValidationResult.fail("PERMISSION_DENIED", "需要角色权限才能访问");
                    }

                    // 检查用户是否拥有所需角色
                    boolean hasRole = userRoles.stream().anyMatch(requiredRoles::contains);
                    if (!hasRole) {
                        return EntryValidationResult.fail("PERMISSION_DENIED", 
                                "用户角色不满足要求，需要角色：" + requiredRoles);
                    }
                }
            }

            // 检查是否需要特定用户
            if (config.containsKey("users")) {
                JSONArray usersArray = config.getJSONArray("users");
                if (usersArray != null && !usersArray.isEmpty()) {
                    List<String> allowedUsers = usersArray.toJavaList(String.class);
                    
                    if (!allowedUsers.contains(userId)) {
                        return EntryValidationResult.fail("PERMISSION_DENIED", 
                                "用户不在允许访问列表中");
                    }
                }
            }

            // 检查是否禁用
            if (config.containsKey("disabled") && config.getBooleanValue("disabled")) {
                return EntryValidationResult.fail("ENTRY_DISABLED", "业务入口已禁用");
            }

            log.debug("[PermissionChecker] 权限检查通过：userId={}, businessType={}", 
                    userId, businessType.getCode());
            return EntryValidationResult.success("权限检查通过");

        } catch (Exception e) {
            log.error("[PermissionChecker] 权限检查异常：userId={}, businessType={}", 
                    userId, businessType.getCode(), e);
            return EntryValidationResult.fail("PERMISSION_CHECK_ERROR", "权限检查异常：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userRoles 用户角色列表
     * @param requiredRole 所需角色
     * @return 是否拥有角色
     */
    public boolean hasRole(List<String> userRoles, String requiredRole) {
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        return userRoles.contains(requiredRole);
    }

    /**
     * 检查用户是否拥有任一指定角色
     *
     * @param userRoles 用户角色列表
     * @param requiredRoles 所需角色列表
     * @return 是否拥有任一角色
     */
    public boolean hasAnyRole(List<String> userRoles, List<String> requiredRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }
        return userRoles.stream().anyMatch(requiredRoles::contains);
    }
}
