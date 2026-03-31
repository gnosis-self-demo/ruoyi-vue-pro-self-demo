package com.gnosis.lifecycle.service;

import com.gnosis.lifecycle.dto.*;


import java.util.List;
import java.util.Map;

/**
 * 配置治理服务接口
 */
public interface GovernanceService {
    
    /**
     * 创建元数据
     */
    Map<String, Object> createMetadata(MetadataCreateRequest request, String operatorId);
    
    /**
     * 获取元数据列表
     */
    List<MetadataVO> listMetadata(String businessTypeId);
    
    /**
     * 获取规则版本历史
     */
    List<RuleVersionHistoryVO> getRuleVersionHistory(String ruleId);
    
    /**
     * 回滚规则版本
     */
    Map<String, Object> rollbackRuleVersion(String versionId, String operatorId);
    
    /**
     * 获取实例追踪
     */
    List<InstanceTraceVO> getInstanceTrace(String businessInstanceId);
    
    /**
     * 获取追踪看板数据
     */
    TraceDashboardVO getTraceDashboard();
}
