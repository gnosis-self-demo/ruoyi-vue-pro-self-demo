package com.gnosis.lifecycle.entry;

import com.gnosis.lifecycle.domain.LifecycleBusinessType;
import com.gnosis.lifecycle.dto.BusinessTypeCreateRequest;
import com.gnosis.lifecycle.dto.BusinessTypeUpdateRequest;
import com.gnosis.lifecycle.repository.LifecycleBusinessTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 业务类型管理器
 * 负责业务类型的 CRUD 操作
 */
@Component
public class BusinessTypeManager {

    private static final Logger log = LoggerFactory.getLogger(BusinessTypeManager.class);

    @Autowired
    private LifecycleBusinessTypeMapper lifecycleBusinessTypeMapper;

    /**
     * 创建业务类型
     *
     * @param request 创建请求
     * @return 创建的实体
     */
    public LifecycleBusinessType create(BusinessTypeCreateRequest request) {
        log.info("[BusinessTypeManager] 创建业务类型：{}", request.getCode());

        // 校验编码是否已存在
        LifecycleBusinessType existing = lifecycleBusinessTypeMapper.selectByCode(request.getCode());
        if (existing != null) {
            throw new IllegalArgumentException("业务类型编码已存在：" + request.getCode());
        }

        LifecycleBusinessType businessType = new LifecycleBusinessType();
        businessType.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
        businessType.setName(request.getName());
        businessType.setCode(request.getCode());
        businessType.setDescription(request.getDescription());
        businessType.setEntryPermissionConfig(request.getEntryPermissionConfig());

        // 设置审计字段
        String userId = getCurrentUserId();
        businessType.setCreateUserId(userId);
        businessType.setUpdateUserId(userId);
        businessType.setCreateTime(new java.util.Date());
        businessType.setUpdateTime(new java.util.Date());

        lifecycleBusinessTypeMapper.insert(businessType);
        log.info("[BusinessTypeManager] 业务类型创建成功：{}", businessType.getId());

        return businessType;
    }

    /**
     * 更新业务类型
     *
     * @param request 更新请求
     * @return 更新后的实体
     */
    public LifecycleBusinessType update(BusinessTypeUpdateRequest request) {
        log.info("[BusinessTypeManager] 更新业务类型：{}", request.getId());

        LifecycleBusinessType existing = lifecycleBusinessTypeMapper.selectById(request.getId());
        if (existing == null) {
            throw new IllegalArgumentException("业务类型不存在：" + request.getId());
        }

        // 如果编码变更，检查新编码是否已存在
        if (request.getCode() != null && !request.getCode().equals(existing.getCode())) {
            LifecycleBusinessType codeExists = lifecycleBusinessTypeMapper.selectByCode(request.getCode());
            if (codeExists != null && !codeExists.getId().equals(request.getId())) {
                throw new IllegalArgumentException("业务类型编码已存在：" + request.getCode());
            }
        }

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getCode() != null) {
            existing.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getEntryPermissionConfig() != null) {
            existing.setEntryPermissionConfig(request.getEntryPermissionConfig());
        }

        // 设置审计字段
        existing.setUpdateUserId(getCurrentUserId());
        existing.setUpdateTime(new java.util.Date());

        lifecycleBusinessTypeMapper.updateById(existing);
        log.info("[BusinessTypeManager] 业务类型更新成功：{}", existing.getId());

        return existing;
    }

    /**
     * 根据 ID 查询业务类型
     *
     * @param id 业务类型 ID
     * @return 业务类型实体
     */
    public LifecycleBusinessType getById(String id) {
        log.debug("[BusinessTypeManager] 查询业务类型：{}", id);
        return lifecycleBusinessTypeMapper.selectById(id);
    }

    /**
     * 根据编码查询业务类型
     *
     * @param code 业务类型编码
     * @return 业务类型实体
     */
    public LifecycleBusinessType getByCode(String code) {
        log.debug("[BusinessTypeManager] 查询业务类型：{}", code);
        return lifecycleBusinessTypeMapper.selectByCode(code);
    }

    /**
     * 查询业务类型列表
     *
     * @param businessType 查询条件
     * @return 业务类型列表
     */
    public List<LifecycleBusinessType> list(LifecycleBusinessType businessType) {
        log.debug("[BusinessTypeManager] 查询业务类型列表");
        return lifecycleBusinessTypeMapper.selectList(businessType);
    }

    /**
     * 删除业务类型
     *
     * @param id 业务类型 ID
     */
    public void delete(String id) {
        log.info("[BusinessTypeManager] 删除业务类型：{}", id);

        LifecycleBusinessType existing = lifecycleBusinessTypeMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("业务类型不存在：" + id);
        }

        lifecycleBusinessTypeMapper.deleteById(id);
        log.info("[BusinessTypeManager] 业务类型删除成功：{}", id);
    }

    /**
     * 批量删除业务类型
     *
     * @param ids 业务类型 ID 数组
     */
    public void batchDelete(String[] ids) {
        log.info("[BusinessTypeManager] 批量删除业务类型：{}", (Object[]) ids);
        int count = lifecycleBusinessTypeMapper.deleteByIds(ids);
        log.info("[BusinessTypeManager] 批量删除成功，删除数量：{}", count);
    }

    /**
     * 获取当前用户 ID
     * TODO: 实际项目中应从安全上下文获取
     *
     * @return 当前用户 ID
     */
    private String getCurrentUserId() {
        // TODO: 从 Spring Security 上下文获取当前登录用户 ID
        // 暂时返回默认值
        return "system";
    }
}
