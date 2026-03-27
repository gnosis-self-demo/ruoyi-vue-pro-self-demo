package lifecycle.entry;

import com.alibaba.fastjson.JSON;
import lifecycle.domain.LifecycleBusinessType;
import lifecycle.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paramcheck.lifecycle.dto.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 入口服务实现类
 */
@Service
public class EntryServiceImpl implements EntryService {

    private static final Logger log = LoggerFactory.getLogger(EntryServiceImpl.class);

    @Autowired
    private BusinessTypeManager businessTypeManager;

    @Autowired
    private EntryValidator entryValidator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessTypeVO register(BusinessTypeCreateRequest request) {
        log.info("[EntryService] 注册业务入口：{}", request.getCode());

        LifecycleBusinessType businessType = businessTypeManager.create(request);

        BusinessTypeVO vo = convertToVO(businessType);
        log.info("[EntryService] 业务入口注册成功：{}", vo.getId());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessTypeVO update(BusinessTypeUpdateRequest request) {
        log.info("[EntryService] 更新业务入口：{}", request.getId());

        LifecycleBusinessType businessType = businessTypeManager.update(request);

        BusinessTypeVO vo = convertToVO(businessType);
        log.info("[EntryService] 业务入口更新成功：{}", vo.getId());

        return vo;
    }

    @Override
    public BusinessTypeVO getById(String id) {
        log.debug("[EntryService] 获取业务入口详情：{}", id);

        LifecycleBusinessType businessType = businessTypeManager.getById(id);
        if (businessType == null) {
            throw new IllegalArgumentException("业务入口不存在：" + id);
        }

        return convertToVO(businessType);
    }

    @Override
    public List<BusinessTypeVO> list(LifecycleBusinessType businessType, int pageNum, int pageSize) {
        log.debug("[EntryService] 获取业务入口列表：pageNum={}, pageSize={}", pageNum, pageSize);

        List<LifecycleBusinessType> businessTypes = businessTypeManager.list(businessType);

        List<BusinessTypeVO> voList = new ArrayList<>();
        for (LifecycleBusinessType bt : businessTypes) {
            voList.add(convertToVO(bt));
        }

        log.debug("[EntryService] 业务入口列表获取成功，数量：{}", voList.size());
        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        log.info("[EntryService] 删除业务入口：{}", id);
        businessTypeManager.delete(id);
        log.info("[EntryService] 业务入口删除成功：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(String[] ids) {
        log.info("[EntryService] 批量删除业务入口，数量：{}", ids.length);
        businessTypeManager.batchDelete(ids);
        log.info("[EntryService] 批量删除业务入口成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchEnable(String[] ids) {
        log.info("[EntryService] 批量启用业务入口，数量：{}", ids.length);

        for (String id : ids) {
            try {
                LifecycleBusinessType businessType = businessTypeManager.getById(id);
                if (businessType != null) {
                    // 更新权限配置，移除 disabled 标志
                    String permissionConfig = businessType.getEntryPermissionConfig();
                    if (permissionConfig != null && !permissionConfig.trim().isEmpty()) {
                        try {
                            com.alibaba.fastjson.JSONObject config = JSON.parseObject(permissionConfig);
                            config.put("disabled", false);
                            businessType.setEntryPermissionConfig(config.toJSONString());
                        } catch (Exception e) {
                            log.warn("[EntryService] 解析权限配置失败，使用默认配置：{}", id, e);
                            businessType.setEntryPermissionConfig("{\"disabled\":false}");
                        }
                    } else {
                        businessType.setEntryPermissionConfig("{\"disabled\":false}");
                    }

                    businessType.setUpdateUserId(getCurrentUserId());
                    businessType.setUpdateTime(new Date());
                    businessTypeManager.update(convertToUpdateRequest(businessType));
                }
            } catch (Exception e) {
                log.error("[EntryService] 启用业务入口失败：{}", id, e);
            }
        }

        log.info("[EntryService] 批量启用业务入口完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDisable(String[] ids) {
        log.info("[EntryService] 批量禁用业务入口，数量：{}", ids.length);

        for (String id : ids) {
            try {
                LifecycleBusinessType businessType = businessTypeManager.getById(id);
                if (businessType != null) {
                    // 更新权限配置，设置 disabled 标志
                    String permissionConfig = businessType.getEntryPermissionConfig();
                    if (permissionConfig != null && !permissionConfig.trim().isEmpty()) {
                        try {
                            com.alibaba.fastjson.JSONObject config = JSON.parseObject(permissionConfig);
                            config.put("disabled", true);
                            businessType.setEntryPermissionConfig(config.toJSONString());
                        } catch (Exception e) {
                            log.warn("[EntryService] 解析权限配置失败，使用默认配置：{}", id, e);
                            businessType.setEntryPermissionConfig("{\"disabled\":true}");
                        }
                    } else {
                        businessType.setEntryPermissionConfig("{\"disabled\":true}");
                    }

                    businessType.setUpdateUserId(getCurrentUserId());
                    businessType.setUpdateTime(new Date());
                    businessTypeManager.update(convertToUpdateRequest(businessType));
                }
            } catch (Exception e) {
                log.error("[EntryService] 禁用业务入口失败：{}", id, e);
            }
        }

        log.info("[EntryService] 批量禁用业务入口完成");
    }

    @Override
    public EntryValidationResult validate(EntryValidationRequest request) {
        log.info("[EntryService] 校验入口数据：businessTypeId={}, userId={}", 
                request.getBusinessTypeId(), request.getUserId());

        // 获取业务类型
        LifecycleBusinessType businessType = businessTypeManager.getById(request.getBusinessTypeId());
        if (businessType == null) {
            return EntryValidationResult.fail("BUSINESS_TYPE_NOT_FOUND", 
                    "业务类型不存在：" + request.getBusinessTypeId());
        }

        // 执行校验
        EntryValidationResult result = entryValidator.validate(request, businessType);

        log.info("[EntryService] 入口校验完成：passed={}, message={}", 
                result.isPassed(), result.getMessage());

        return result;
    }

    /**
     * 转换为 VO
     */
    private BusinessTypeVO convertToVO(LifecycleBusinessType businessType) {
        BusinessTypeVO vo = new BusinessTypeVO();
        BeanUtils.copyProperties(businessType, vo);
        return vo;
    }

    /**
     * 转换为更新请求
     */
    private BusinessTypeUpdateRequest convertToUpdateRequest(LifecycleBusinessType businessType) {
        BusinessTypeUpdateRequest request = new BusinessTypeUpdateRequest();
        request.setId(businessType.getId());
        request.setName(businessType.getName());
        request.setCode(businessType.getCode());
        request.setDescription(businessType.getDescription());
        request.setEntryPermissionConfig(businessType.getEntryPermissionConfig());
        return request;
    }

    /**
     * 获取当前用户 ID
     * TODO: 实际项目中应从安全上下文获取
     */
    private String getCurrentUserId() {
        // TODO: 从 Spring Security 上下文获取当前登录用户 ID
        return "system";
    }
}
