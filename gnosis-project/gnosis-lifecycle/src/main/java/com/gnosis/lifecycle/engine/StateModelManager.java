package com.gnosis.lifecycle.engine;

import com.gnosis.lifecycle.domain.LifecycleBusinessType;
import com.gnosis.lifecycle.domain.LifecycleState;
import com.gnosis.lifecycle.domain.LifecycleStateTransition;
import com.gnosis.lifecycle.dto.StateModelCreateRequest;
import com.gnosis.lifecycle.repository.LifecycleBusinessTypeMapper;
import com.gnosis.lifecycle.repository.LifecycleStateMapper;
import com.gnosis.lifecycle.repository.LifecycleStateTransitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 状态模型管理器
 * 负责状态模型的 CRUD 操作
 */
@Component
public class StateModelManager {

    private static final Logger log = LoggerFactory.getLogger(StateModelManager.class);

    @Autowired
    private LifecycleStateMapper stateMapper;

    @Autowired
    private LifecycleStateTransitionMapper transitionMapper;

    @Autowired
    private LifecycleBusinessTypeMapper businessTypeMapper;

    /**
     * 创建状态模型
     *
     * @param request 创建请求
     * @return 创建的状态模型 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String create(StateModelCreateRequest request) {
        log.info("[StateModelManager] 创建状态模型：businessTypeId={}, modelCode={}", 
                request.getBusinessTypeId(), request.getModelCode());

        // 校验业务类型是否存在
        LifecycleBusinessType businessType = businessTypeMapper.selectById(request.getBusinessTypeId());
        if (businessType == null) {
            throw new IllegalArgumentException("业务类型不存在：" + request.getBusinessTypeId());
        }

        String userId = getCurrentUserId();
        Date now = new Date();

        // 创建状态节点
        List<String> stateIds = new ArrayList<>();
        if (request.getStates() != null) {
            for (StateModelCreateRequest.StateNodeConfig stateConfig : request.getStates()) {
                LifecycleState state = new LifecycleState();
                state.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
                state.setBusinessTypeId(request.getBusinessTypeId());
                state.setStateName(stateConfig.getStateName());
                state.setStateCode(stateConfig.getStateCode());
                state.setStateType(stateConfig.getStateType());
                state.setPositionX(stateConfig.getPositionX());
                state.setPositionY(stateConfig.getPositionY());
                state.setMetadata(stateConfig.getMetadata());
                
                // 设置是否终态
                if ("final".equals(stateConfig.getStateType())) {
                    state.setIsFinalState(1);
                } else {
                    state.setIsFinalState(0);
                }

                state.setCreateUserId(userId);
                state.setUpdateUserId(userId);
                state.setCreateTime(now);
                state.setUpdateTime(now);

                stateMapper.insert(state);
                stateIds.add(state.getId());
                log.debug("[StateModelManager] 创建状态节点：{}", state.getStateCode());
            }
        }

        // 创建状态流转
        if (request.getTransitions() != null) {
            for (StateModelCreateRequest.StateTransitionConfig transitionConfig : request.getTransitions()) {
                // 根据状态编码查询状态 ID
                LifecycleState fromState = stateMapper.selectByCodeAndBusinessTypeId(
                        transitionConfig.getFromStateCode(), request.getBusinessTypeId());
                LifecycleState toState = stateMapper.selectByCodeAndBusinessTypeId(
                        transitionConfig.getToStateCode(), request.getBusinessTypeId());

                if (fromState == null) {
                    throw new IllegalArgumentException("源状态不存在：" + transitionConfig.getFromStateCode());
                }
                if (toState == null) {
                    throw new IllegalArgumentException("目标状态不存在：" + transitionConfig.getToStateCode());
                }

                LifecycleStateTransition transition = new LifecycleStateTransition();
                transition.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
                transition.setBusinessTypeId(request.getBusinessTypeId());
                transition.setFromStateId(fromState.getId());
                transition.setToStateId(toState.getId());
                transition.setTransitionName(transitionConfig.getTransitionName());
                transition.setTransitionCode(transitionConfig.getTransitionCode());
                transition.setConditionExpression(transitionConfig.getConditionExpression());
                transition.setPriority(transitionConfig.getPriority() != null ? transitionConfig.getPriority() : 0);

                transition.setCreateUserId(userId);
                transition.setUpdateUserId(userId);
                transition.setCreateTime(now);
                transition.setUpdateTime(now);

                transitionMapper.insert(transition);
                log.debug("[StateModelManager] 创建状态流转：{}", transition.getTransitionCode());
            }
        }

        log.info("[StateModelManager] 状态模型创建成功：businessTypeId={}", request.getBusinessTypeId());
        return request.getBusinessTypeId();
    }

    /**
     * 获取状态模型
     *
     * @param businessTypeId 业务类型 ID
     * @return 状态模型信息
     */
    public StateModelCreateRequest getModel(String businessTypeId) {
        log.debug("[StateModelManager] 获取状态模型：businessTypeId={}", businessTypeId);

        // 查询状态节点
        List<LifecycleState> states = stateMapper.selectByBusinessTypeId(businessTypeId);
        
        // 查询状态流转
        List<LifecycleStateTransition> transitions = transitionMapper.selectByBusinessTypeId(businessTypeId);

        StateModelCreateRequest model = new StateModelCreateRequest();
        model.setBusinessTypeId(businessTypeId);

        // 转换为配置对象
        List<StateModelCreateRequest.StateNodeConfig> stateConfigs = new ArrayList<>();
        for (LifecycleState state : states) {
            StateModelCreateRequest.StateNodeConfig config = new StateModelCreateRequest.StateNodeConfig();
            config.setStateName(state.getStateName());
            config.setStateCode(state.getStateCode());
            config.setStateType(state.getStateType());
            config.setPositionX(state.getPositionX());
            config.setPositionY(state.getPositionY());
            config.setMetadata(state.getMetadata());
            stateConfigs.add(config);
        }
        model.setStates(stateConfigs);

        List<StateModelCreateRequest.StateTransitionConfig> transitionConfigs = new ArrayList<>();
        for (LifecycleStateTransition transition : transitions) {
            StateModelCreateRequest.StateTransitionConfig config = new StateModelCreateRequest.StateTransitionConfig();
            config.setTransitionName(transition.getTransitionName());
            config.setTransitionCode(transition.getTransitionCode());
            config.setConditionExpression(transition.getConditionExpression());
            config.setPriority(transition.getPriority());
            
            // 需要查询状态编码
            LifecycleState fromState = stateMapper.selectById(transition.getFromStateId());
            LifecycleState toState = stateMapper.selectById(transition.getToStateId());
            if (fromState != null) {
                config.setFromStateCode(fromState.getStateCode());
            }
            if (toState != null) {
                config.setToStateCode(toState.getStateCode());
            }
            
            transitionConfigs.add(config);
        }
        model.setTransitions(transitionConfigs);

        return model;
    }

    /**
     * 更新状态模型
     *
     * @param businessTypeId 业务类型 ID
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(String businessTypeId, StateModelCreateRequest request) {
        log.info("[StateModelManager] 更新状态模型：businessTypeId={}", businessTypeId);

        // 先删除原有的状态节点和流转
        stateMapper.deleteByBusinessTypeId(businessTypeId);
        transitionMapper.deleteByBusinessTypeId(businessTypeId);

        // 重新创建
        request.setBusinessTypeId(businessTypeId);
        create(request);

        log.info("[StateModelManager] 状态模型更新成功：businessTypeId={}", businessTypeId);
    }

    /**
     * 删除状态模型
     *
     * @param businessTypeId 业务类型 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String businessTypeId) {
        log.info("[StateModelManager] 删除状态模型：businessTypeId={}", businessTypeId);

        // 删除状态流转
        transitionMapper.deleteByBusinessTypeId(businessTypeId);
        
        // 删除状态节点
        stateMapper.deleteByBusinessTypeId(businessTypeId);

        log.info("[StateModelManager] 状态模型删除成功：businessTypeId={}", businessTypeId);
    }

    /**
     * 获取当前用户 ID
     * TODO: 实际项目中应从安全上下文获取
     */
    private String getCurrentUserId() {
        return "system";
    }
}
