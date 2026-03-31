package com.gnosis.lifecycle.service;

import com.gnosis.lifecycle.domain.LifecycleInstanceTrace;
import com.gnosis.lifecycle.domain.LifecycleMetadataDictionary;
import com.gnosis.lifecycle.domain.LifecycleRouteRuleVersion;
import com.gnosis.lifecycle.dto.*;
import com.gnosis.lifecycle.repository.LifecycleInstanceTraceMapper;
import com.gnosis.lifecycle.repository.LifecycleMetadataDictionaryMapper;
import com.gnosis.lifecycle.repository.LifecycleRouteRuleMapper;
import com.gnosis.lifecycle.repository.LifecycleRouteRuleVersionMapper;
import com.gnosis.lifecycle.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 配置治理服务实现类
 */
@Service
public class GovernanceServiceImpl implements GovernanceService {
    
    private static final Logger log = LoggerFactory.getLogger(GovernanceServiceImpl.class);
    
    @Autowired
    private LifecycleMetadataDictionaryMapper metadataDictionaryMapper;
    
    @Autowired
    private LifecycleRouteRuleVersionMapper ruleVersionMapper;
    
    @Autowired
    private LifecycleInstanceTraceMapper instanceTraceMapper;
    
    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;
    
    @Autowired
    private IdGenerator idGenerator;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createMetadata(MetadataCreateRequest request, String operatorId) {
        log.info("[GovernanceService] 创建元数据：businessTypeId={}, fieldName={}", 
                request.getBusinessTypeId(), request.getFieldName());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 检查字段是否已存在
            LifecycleMetadataDictionary existing = metadataDictionaryMapper.selectByBusinessTypeId(request.getBusinessTypeId())
                    .stream()
                    .filter(m -> request.getFieldName().equals(m.getFieldName()))
                    .findFirst()
                    .orElse(null);
            
            if (existing != null) {
                result.put("success", false);
                result.put("message", "字段已存在：" + request.getFieldName());
                return result;
            }
            
            // 2. 创建元数据
            LifecycleMetadataDictionary metadata = new LifecycleMetadataDictionary();
            metadata.setId(idGenerator.generate());
            metadata.setBusinessTypeId(request.getBusinessTypeId());
            metadata.setFieldName(request.getFieldName());
            metadata.setFieldLabel(request.getFieldLabel());
            metadata.setFieldType(request.getFieldType());
            metadata.setFieldLength(request.getFieldLength());
            metadata.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : 0);
            metadata.setIsSearchable(request.getIsSearchable() != null ? request.getIsSearchable() : 0);
            metadata.setIsDisplay(request.getIsDisplay() != null ? request.getIsDisplay() : 1);
            metadata.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
            metadata.setEnumValues(request.getEnumValues());
            metadata.setValidationRule(request.getValidationRule());
            metadata.setDefaultValue(request.getDefaultValue());
            metadata.setRemark(request.getRemark());
            metadata.setCreateUserId(operatorId);
            metadata.setUpdateUserId(operatorId);
            metadata.setCreateTime(new Date());
            metadata.setUpdateTime(new Date());
            
            int rows = metadataDictionaryMapper.insert(metadata);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "元数据创建成功");
                result.put("id", metadata.getId());
                log.info("[GovernanceService] 元数据创建成功，id={}", metadata.getId());
            } else {
                result.put("success", false);
                result.put("message", "元数据创建失败");
                log.error("[GovernanceService] 元数据创建失败");
            }
            
        } catch (Exception e) {
            log.error("[GovernanceService] 创建元数据异常", e);
            result.put("success", false);
            result.put("message", "创建异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    public List<MetadataVO> listMetadata(String businessTypeId) {
        log.info("[GovernanceService] 查询元数据列表：businessTypeId={}", businessTypeId);
        
        List<MetadataVO> voList = new ArrayList<>();
        
        try {
            List<LifecycleMetadataDictionary> metadataList = metadataDictionaryMapper.selectByBusinessTypeId(businessTypeId);
            
            for (LifecycleMetadataDictionary metadata : metadataList) {
                MetadataVO vo = new MetadataVO();
                BeanUtils.copyProperties(metadata, vo);
                voList.add(vo);
            }
            
        } catch (Exception e) {
            log.error("[GovernanceService] 查询元数据列表异常", e);
        }
        
        return voList;
    }
    
    @Override
    public List<RuleVersionHistoryVO> getRuleVersionHistory(String ruleId) {
        log.info("[GovernanceService] 查询规则版本历史：ruleId={}", ruleId);
        
        List<RuleVersionHistoryVO> voList = new ArrayList<>();
        
        try {
            List<LifecycleRouteRuleVersion> versionList = ruleVersionMapper.selectByRuleId(ruleId);
            
            for (LifecycleRouteRuleVersion version : versionList) {
                RuleVersionHistoryVO vo = new RuleVersionHistoryVO();
                BeanUtils.copyProperties(version, vo);
                voList.add(vo);
            }
            
        } catch (Exception e) {
            log.error("[GovernanceService] 查询规则版本历史异常", e);
        }
        
        return voList;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> rollbackRuleVersion(String versionId, String operatorId) {
        log.info("[GovernanceService] 回滚规则版本：versionId={}", versionId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 查询要回滚的版本
            LifecycleRouteRuleVersion targetVersion = ruleVersionMapper.selectById(versionId);
            if (targetVersion == null) {
                result.put("success", false);
                result.put("message", "版本不存在");
                return result;
            }
            
            // 2. 将当前版本标记为非当前版本
            LifecycleRouteRuleVersion currentVersion = new LifecycleRouteRuleVersion();
            currentVersion.setIsCurrentVersion(0);
            currentVersion.setUpdateTime(new Date());
            currentVersion.setUpdateUserId(operatorId);
            
            // 3. 创建新版本（回滚版本）
            LifecycleRouteRuleVersion newVersion = new LifecycleRouteRuleVersion();
            newVersion.setId(idGenerator.generate());
            newVersion.setRuleId(targetVersion.getRuleId());
            newVersion.setVersionNumber(targetVersion.getVersionNumber() + 1);
            newVersion.setRuleContent(targetVersion.getRuleContent());
            newVersion.setChangeType("ROLLBACK");
            newVersion.setChangeReason("回滚到版本 " + targetVersion.getVersionNumber());
            newVersion.setIsCurrentVersion(1);
            newVersion.setRollbackFromVersion(targetVersion.getVersionNumber());
            newVersion.setRemark("从版本 " + targetVersion.getVersionNumber() + " 回滚");
            newVersion.setCreateUserId(operatorId);
            newVersion.setUpdateUserId(operatorId);
            newVersion.setCreateTime(new Date());
            newVersion.setUpdateTime(new Date());
            
            // 4. 插入新版本
            int rows = ruleVersionMapper.insert(newVersion);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "规则版本回滚成功");
                result.put("newVersionId", newVersion.getId());
                result.put("newVersionNumber", newVersion.getVersionNumber());
                log.info("[GovernanceService] 规则版本回滚成功，新版本 id={}", newVersion.getId());
            } else {
                result.put("success", false);
                result.put("message", "规则版本回滚失败");
                log.error("[GovernanceService] 规则版本回滚失败");
            }
            
        } catch (Exception e) {
            log.error("[GovernanceService] 回滚规则版本异常", e);
            result.put("success", false);
            result.put("message", "回滚异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    public List<InstanceTraceVO> getInstanceTrace(String businessInstanceId) {
        log.info("[GovernanceService] 查询实例追踪：businessInstanceId={}", businessInstanceId);
        
        List<InstanceTraceVO> voList = new ArrayList<>();
        
        try {
            List<LifecycleInstanceTrace> traceList = instanceTraceMapper.selectByBusinessInstanceId(businessInstanceId);
            
            for (LifecycleInstanceTrace trace : traceList) {
                InstanceTraceVO vo = new InstanceTraceVO();
                BeanUtils.copyProperties(trace, vo);
                voList.add(vo);
            }
            
        } catch (Exception e) {
            log.error("[GovernanceService] 查询实例追踪异常", e);
        }
        
        return voList;
    }
    
    @Override
    public TraceDashboardVO getTraceDashboard() {
        log.info("[GovernanceService] 查询追踪看板数据");
        
        TraceDashboardVO dashboard = new TraceDashboardVO();
        
        try {
            // 1. 查询总实例数和今日新增
            // TODO: 实现统计查询
            dashboard.setTotalInstances(0);
            dashboard.setTodayInstances(0);
            
            // 2. 查询总事件数和今日事件
            dashboard.setTotalEvents(0);
            dashboard.setTodayEvents(0);
            
            // 3. 查询状态统计
            dashboard.setStateStatistics(new ArrayList<>());
            
            // 4. 查询事件统计
            dashboard.setEventStatistics(new ArrayList<>());
            
            // 5. 查询流转统计
            dashboard.setTransitionStatistics(new ArrayList<>());
            
        } catch (Exception e) {
            log.error("[GovernanceService] 查询追踪看板数据异常", e);
        }
        
        return dashboard;
    }
}
