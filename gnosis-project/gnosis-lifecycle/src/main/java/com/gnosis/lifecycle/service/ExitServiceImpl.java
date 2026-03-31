package com.gnosis.lifecycle.service;

import com.alibaba.fastjson.JSON;
import com.gnosis.lifecycle.domain.LifecycleDownstreamTriggerConfig;
import com.gnosis.lifecycle.domain.LifecycleEventPublishRecord;
import com.gnosis.lifecycle.domain.LifecycleFinalStateConfig;
import com.gnosis.lifecycle.dto.DownstreamConfigRequest;
import com.gnosis.lifecycle.dto.DownstreamConfigVO;
import com.gnosis.lifecycle.dto.EventPublishRequest;
import com.gnosis.lifecycle.dto.FinalStateConfigRequest;
import com.gnosis.lifecycle.repository.LifecycleDownstreamTriggerConfigMapper;
import com.gnosis.lifecycle.repository.LifecycleEventPublishRecordMapper;
import com.gnosis.lifecycle.repository.LifecycleFinalStateConfigMapper;
import com.gnosis.lifecycle.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 出口服务实现类
 */
@Service
public class ExitServiceImpl implements ExitService {
    
    private static final Logger log = LoggerFactory.getLogger(ExitServiceImpl.class);
    
    @Autowired
    private LifecycleFinalStateConfigMapper finalStateConfigMapper;
    
    @Autowired
    private LifecycleDownstreamTriggerConfigMapper downstreamTriggerConfigMapper;
    
    @Autowired
    private LifecycleEventPublishRecordMapper eventPublishRecordMapper;
    
    @Autowired
    private IdGenerator idGenerator;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> configureFinalState(FinalStateConfigRequest request, String operatorId) {
        log.info("[ExitService] 配置终态：businessTypeId={}, stateId={}", 
                request.getBusinessTypeId(), request.getStateId());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 创建终态配置
            LifecycleFinalStateConfig config = new LifecycleFinalStateConfig();
            config.setId(idGenerator.generate());
            config.setBusinessTypeId(request.getBusinessTypeId());
            config.setStateId(request.getStateId());
            config.setStateCode(request.getStateCode());
            config.setStateName(request.getStateName());
            config.setFinalActionType(request.getFinalActionType());
            config.setFinalActionConfig(request.getFinalActionConfig());
            config.setAllowReactivate(request.getAllowReactivate() != null ? request.getAllowReactivate() : 0);
            config.setRemark(request.getRemark());
            config.setCreateUserId(operatorId);
            config.setUpdateUserId(operatorId);
            config.setCreateTime(new Date());
            config.setUpdateTime(new Date());
            
            int rows = finalStateConfigMapper.insert(config);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "终态配置成功");
                result.put("id", config.getId());
                log.info("[ExitService] 终态配置成功，id={}", config.getId());
            } else {
                result.put("success", false);
                result.put("message", "终态配置失败");
                log.error("[ExitService] 终态配置失败");
            }
            
        } catch (Exception e) {
            log.error("[ExitService] 配置终态异常", e);
            result.put("success", false);
            result.put("message", "配置异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> listFinalStates(String businessTypeId) {
        log.info("[ExitService] 查询终态配置列表：businessTypeId={}", businessTypeId);
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        try {
            LifecycleFinalStateConfig query = new LifecycleFinalStateConfig();
            if (businessTypeId != null && !businessTypeId.isEmpty()) {
                query.setBusinessTypeId(businessTypeId);
            }
            
            List<LifecycleFinalStateConfig> configs = finalStateConfigMapper.selectList(query);
            
            for (LifecycleFinalStateConfig config : configs) {
                Map<String, Object> map = convertToMap(config);
                resultList.add(map);
            }
            
        } catch (Exception e) {
            log.error("[ExitService] 查询终态配置列表异常", e);
        }
        
        return resultList;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> configureDownstream(DownstreamConfigRequest request, String operatorId) {
        log.info("[ExitService] 配置下游触发：finalStateId={}, triggerType={}", 
                request.getFinalStateId(), request.getTriggerType());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 验证终态配置是否存在
            LifecycleFinalStateConfig finalState = finalStateConfigMapper.selectById(request.getFinalStateId());
            if (finalState == null) {
                result.put("success", false);
                result.put("message", "终态配置不存在");
                return result;
            }
            
            // 2. 创建下游触发配置
            LifecycleDownstreamTriggerConfig config = new LifecycleDownstreamTriggerConfig();
            config.setId(idGenerator.generate());
            config.setFinalStateId(request.getFinalStateId());
            config.setTriggerType(request.getTriggerType());
            config.setTriggerUrl(request.getTriggerUrl());
            config.setTriggerMethod(request.getTriggerMethod());
            config.setTriggerHeaders(request.getTriggerHeaders());
            config.setTriggerPayload(request.getTriggerPayload());
            config.setMqTopic(request.getMqTopic());
            config.setMqTag(request.getMqTag());
            config.setMqMessageTemplate(request.getMqMessageTemplate());
            config.setEmailRecipients(request.getEmailRecipients());
            config.setEmailTemplate(request.getEmailTemplate());
            config.setSmsRecipients(request.getSmsRecipients());
            config.setSmsTemplate(request.getSmsTemplate());
            config.setRetryTimes(request.getRetryTimes() != null ? request.getRetryTimes() : 3);
            config.setRetryInterval(request.getRetryInterval() != null ? request.getRetryInterval() : 60);
            config.setTimeoutSeconds(request.getTimeoutSeconds() != null ? request.getTimeoutSeconds() : 30);
            config.setIsAsync(request.getIsAsync() != null ? request.getIsAsync() : 1);
            config.setIsActive(request.getIsActive() != null ? request.getIsActive() : 1);
            config.setTriggerCondition(request.getTriggerCondition());
            config.setRemark(request.getRemark());
            config.setCreateUserId(operatorId);
            config.setUpdateUserId(operatorId);
            config.setCreateTime(new Date());
            config.setUpdateTime(new Date());
            
            int rows = downstreamTriggerConfigMapper.insert(config);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "下游触发配置成功");
                result.put("id", config.getId());
                log.info("[ExitService] 下游触发配置成功，id={}", config.getId());
            } else {
                result.put("success", false);
                result.put("message", "下游触发配置失败");
                log.error("[ExitService] 下游触发配置失败");
            }
            
        } catch (Exception e) {
            log.error("[ExitService] 配置下游触发异常", e);
            result.put("success", false);
            result.put("message", "配置异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    public DownstreamConfigVO getDownstreamConfig(String id) {
        log.info("[ExitService] 查询下游配置详情：id={}", id);
        
        try {
            LifecycleDownstreamTriggerConfig config = downstreamTriggerConfigMapper.selectById(id);
            if (config == null) {
                throw new IllegalArgumentException("下游配置不存在：" + id);
            }
            
            DownstreamConfigVO vo = new DownstreamConfigVO();
            BeanUtils.copyProperties(config, vo);
            
            return vo;
            
        } catch (Exception e) {
            log.error("[ExitService] 查询下游配置详情异常", e);
            throw e;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateDownstream(DownstreamConfigRequest request, String operatorId) {
        log.info("[ExitService] 更新下游配置：id={}", request.getId());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 验证配置是否存在
            LifecycleDownstreamTriggerConfig existing = downstreamTriggerConfigMapper.selectById(request.getId());
            if (existing == null) {
                result.put("success", false);
                result.put("message", "下游配置不存在");
                return result;
            }
            
            // 2. 更新配置
            LifecycleDownstreamTriggerConfig config = new LifecycleDownstreamTriggerConfig();
            config.setId(request.getId());
            config.setFinalStateId(request.getFinalStateId());
            config.setTriggerType(request.getTriggerType());
            config.setTriggerUrl(request.getTriggerUrl());
            config.setTriggerMethod(request.getTriggerMethod());
            config.setTriggerHeaders(request.getTriggerHeaders());
            config.setTriggerPayload(request.getTriggerPayload());
            config.setMqTopic(request.getMqTopic());
            config.setMqTag(request.getMqTag());
            config.setMqMessageTemplate(request.getMqMessageTemplate());
            config.setEmailRecipients(request.getEmailRecipients());
            config.setEmailTemplate(request.getEmailTemplate());
            config.setSmsRecipients(request.getSmsRecipients());
            config.setSmsTemplate(request.getSmsTemplate());
            config.setRetryTimes(request.getRetryTimes());
            config.setRetryInterval(request.getRetryInterval());
            config.setTimeoutSeconds(request.getTimeoutSeconds());
            config.setIsAsync(request.getIsAsync());
            config.setIsActive(request.getIsActive());
            config.setTriggerCondition(request.getTriggerCondition());
            config.setRemark(request.getRemark());
            config.setUpdateUserId(operatorId);
            config.setUpdateTime(new Date());
            
            int rows = downstreamTriggerConfigMapper.updateById(config);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "下游配置更新成功");
                log.info("[ExitService] 下游配置更新成功，id={}", request.getId());
            } else {
                result.put("success", false);
                result.put("message", "下游配置更新失败");
                log.error("[ExitService] 下游配置更新失败");
            }
            
        } catch (Exception e) {
            log.error("[ExitService] 更新下游配置异常", e);
            result.put("success", false);
            result.put("message", "更新异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> deleteDownstream(String id, String operatorId) {
        log.info("[ExitService] 删除下游配置：id={}", id);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int rows = downstreamTriggerConfigMapper.deleteById(id);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "下游配置删除成功");
                log.info("[ExitService] 下游配置删除成功，id={}", id);
            } else {
                result.put("success", false);
                result.put("message", "下游配置删除失败");
                log.error("[ExitService] 下游配置删除失败");
            }
            
        } catch (Exception e) {
            log.error("[ExitService] 删除下游配置异常", e);
            result.put("success", false);
            result.put("message", "删除异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchDeleteDownstream(String[] ids, String operatorId) {
        log.info("[ExitService] 批量删除下游配置：ids={}", Arrays.toString(ids));
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int rows = downstreamTriggerConfigMapper.deleteByIds(ids);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "批量删除下游配置成功，删除" + rows + "条记录");
                log.info("[ExitService] 批量删除下游配置成功，删除{}条记录", rows);
            } else {
                result.put("success", false);
                result.put("message", "批量删除下游配置失败");
                log.error("[ExitService] 批量删除下游配置失败");
            }
            
        } catch (Exception e) {
            log.error("[ExitService] 批量删除下游配置异常", e);
            result.put("success", false);
            result.put("message", "批量删除异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> publishEvent(EventPublishRequest request, String operatorId) {
        log.info("[ExitService] 发布事件：eventId={}, publishType={}", 
                request.getEventId(), request.getPublishType());
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 创建事件发布记录
            LifecycleEventPublishRecord record = new LifecycleEventPublishRecord();
            record.setId(idGenerator.generate());
            record.setEventId(request.getEventId());
            record.setBusinessTypeId(request.getBusinessTypeId());
            record.setBusinessInstanceId(request.getBusinessInstanceId());
            record.setPublishType(request.getPublishType());
            record.setTargetType(request.getTargetType());
            record.setTargetConfig(JSON.toJSONString(request.getTargetConfig()));
            record.setPayload(JSON.toJSONString(request.getPayload()));
            record.setPublishStatus("PENDING");
            record.setRetryCount(0);
            record.setRemark(request.getRemark());
            record.setCreateUserId(operatorId);
            record.setUpdateUserId(operatorId);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            
            int rows = eventPublishRecordMapper.insert(record);
            
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "事件发布记录创建成功");
                result.put("id", record.getId());
                
                // 2. 如果是同步发布，这里可以立即执行发布逻辑
                if ("SYNC".equals(request.getPublishType())) {
                    // TODO: 实现同步发布逻辑
                    log.info("[ExitService] 执行同步发布");
                }
                
                log.info("[ExitService] 事件发布记录创建成功，id={}", record.getId());
            } else {
                result.put("success", false);
                result.put("message", "事件发布记录创建失败");
                log.error("[ExitService] 事件发布记录创建失败");
            }
            
        } catch (Exception e) {
            log.error("[ExitService] 发布事件异常", e);
            result.put("success", false);
            result.put("message", "发布异常：" + e.getMessage());
            throw e;
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getPublishRecord(String eventId) {
        log.info("[ExitService] 查询事件发布记录：eventId={}", eventId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            LifecycleEventPublishRecord record = eventPublishRecordMapper.selectByEventId(eventId);
            if (record == null) {
                result.put("success", false);
                result.put("message", "事件发布记录不存在");
                return result;
            }
            
            result.put("success", true);
            result.put("data", convertToMap(record));
            
        } catch (Exception e) {
            log.error("[ExitService] 查询事件发布记录异常", e);
            result.put("success", false);
            result.put("message", "查询异常：" + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 转换为 Map
     */
    private Map<String, Object> convertToMap(LifecycleFinalStateConfig config) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", config.getId());
        map.put("businessTypeId", config.getBusinessTypeId());
        map.put("stateId", config.getStateId());
        map.put("stateCode", config.getStateCode());
        map.put("stateName", config.getStateName());
        map.put("finalActionType", config.getFinalActionType());
        map.put("finalActionConfig", config.getFinalActionConfig());
        map.put("allowReactivate", config.getAllowReactivate());
        map.put("remark", config.getRemark());
        map.put("createUserId", config.getCreateUserId());
        map.put("updateUserId", config.getUpdateUserId());
        map.put("createTime", config.getCreateTime());
        map.put("updateTime", config.getUpdateTime());
        return map;
    }
    
    /**
     * 转换为 Map
     */
    private Map<String, Object> convertToMap(LifecycleEventPublishRecord record) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", record.getId());
        map.put("eventId", record.getEventId());
        map.put("businessTypeId", record.getBusinessTypeId());
        map.put("businessInstanceId", record.getBusinessInstanceId());
        map.put("publishType", record.getPublishType());
        map.put("targetType", record.getTargetType());
        map.put("targetConfig", record.getTargetConfig());
        map.put("payload", record.getPayload());
        map.put("publishStatus", record.getPublishStatus());
        map.put("retryCount", record.getRetryCount());
        map.put("errorMessage", record.getErrorMessage());
        map.put("responseData", record.getResponseData());
        map.put("publishTime", record.getPublishTime());
        map.put("remark", record.getRemark());
        map.put("createUserId", record.getCreateUserId());
        map.put("updateUserId", record.getUpdateUserId());
        map.put("createTime", record.getCreateTime());
        map.put("updateTime", record.getUpdateTime());
        return map;
    }
}
