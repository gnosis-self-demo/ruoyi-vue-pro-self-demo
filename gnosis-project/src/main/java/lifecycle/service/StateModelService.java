package lifecycle.service;

import lifecycle.domain.LifecycleBusinessType;
import lifecycle.domain.LifecycleState;
import lifecycle.domain.LifecycleStateTransition;
import lifecycle.dto.StateModelCreateRequest;
import lifecycle.dto.StateModelVO;
import lifecycle.repository.LifecycleBusinessTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lifecycle.engine.StateModelManager;
import lifecycle.repository.LifecycleStateMapper;
import lifecycle.repository.LifecycleStateTransitionMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 状态模型服务
 */
@Service
public class StateModelService {

    private static final Logger log = LoggerFactory.getLogger(StateModelService.class);

    @Autowired
    private StateModelManager stateModelManager;

    @Autowired
    private LifecycleBusinessTypeMapper businessTypeMapper;

    @Autowired
    private LifecycleStateMapper stateMapper;

    @Autowired
    private LifecycleStateTransitionMapper transitionMapper;

    /**
     * 创建状态模型
     *
     * @param request 创建请求
     * @return 状态模型 VO
     */
    @Transactional(rollbackFor = Exception.class)
    public StateModelVO create(StateModelCreateRequest request) {
        log.info("[StateModelService] 创建状态模型：businessTypeId={}, modelCode={}", 
                request.getBusinessTypeId(), request.getModelCode());

        String businessTypeId = stateModelManager.create(request);

        return getModel(businessTypeId);
    }

    /**
     * 获取状态模型
     *
     * @param businessTypeId 业务类型 ID
     * @return 状态模型 VO
     */
    public StateModelVO getModel(String businessTypeId) {
        log.debug("[StateModelService] 获取状态模型：businessTypeId={}", businessTypeId);

        // 获取业务类型信息
        LifecycleBusinessType businessType = businessTypeMapper.selectById(businessTypeId);
        if (businessType == null) {
            throw new IllegalArgumentException("业务类型不存在：" + businessTypeId);
        }

        // 获取状态模型
        StateModelCreateRequest model = stateModelManager.getModel(businessTypeId);

        // 转换为 VO
        StateModelVO vo = convertToVO(model, businessType);
        log.debug("[StateModelService] 状态模型获取成功：businessTypeId={}", businessTypeId);

        return vo;
    }

    /**
     * 更新状态模型
     *
     * @param businessTypeId 业务类型 ID
     * @param request 更新请求
     * @return 状态模型 VO
     */
    @Transactional(rollbackFor = Exception.class)
    public StateModelVO update(String businessTypeId, StateModelCreateRequest request) {
        log.info("[StateModelService] 更新状态模型：businessTypeId={}", businessTypeId);

        stateModelManager.update(businessTypeId, request);

        return getModel(businessTypeId);
    }

    /**
     * 删除状态模型
     *
     * @param businessTypeId 业务类型 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String businessTypeId) {
        log.info("[StateModelService] 删除状态模型：businessTypeId={}", businessTypeId);
        stateModelManager.delete(businessTypeId);
        log.info("[StateModelService] 状态模型删除成功：businessTypeId={}", businessTypeId);
    }

    /**
     * 转换为 VO
     */
    private StateModelVO convertToVO(StateModelCreateRequest model, LifecycleBusinessType businessType) {
        StateModelVO vo = new StateModelVO();
        vo.setBusinessTypeId(model.getBusinessTypeId());
        vo.setBusinessTypeName(businessType.getName());
        vo.setModelName(model.getModelName());
        vo.setModelCode(model.getModelCode());
        vo.setDescription(model.getDescription());

        // 查询状态节点
        List<LifecycleState> states = stateMapper.selectByBusinessTypeId(model.getBusinessTypeId());
        List<StateModelVO.StateNodeVO> stateVOs = new ArrayList<>();
        for (LifecycleState state : states) {
            StateModelVO.StateNodeVO stateVO = new StateModelVO.StateNodeVO();
            stateVO.setId(state.getId());
            stateVO.setStateName(state.getStateName());
            stateVO.setStateCode(state.getStateCode());
            stateVO.setStateType(state.getStateType());
            stateVO.setIsFinalState(state.getIsFinalState());
            stateVO.setPositionX(state.getPositionX());
            stateVO.setPositionY(state.getPositionY());
            stateVO.setMetadata(state.getMetadata());
            stateVOs.add(stateVO);
        }
        vo.setStates(stateVOs);

        // 查询状态流转
        List<LifecycleStateTransition> transitions = transitionMapper.selectByBusinessTypeId(model.getBusinessTypeId());
        List<StateModelVO.StateTransitionVO> transitionVOs = new ArrayList<>();
        for (LifecycleStateTransition transition : transitions) {
            StateModelVO.StateTransitionVO transitionVO = new StateModelVO.StateTransitionVO();
            transitionVO.setId(transition.getId());
            transitionVO.setTransitionName(transition.getTransitionName());
            transitionVO.setTransitionCode(transition.getTransitionCode());
            transitionVO.setFromStateId(transition.getFromStateId());
            transitionVO.setToStateId(transition.getToStateId());
            transitionVO.setConditionExpression(transition.getConditionExpression());
            transitionVO.setPriority(transition.getPriority());

            // 查询状态编码
            LifecycleState fromState = stateMapper.selectById(transition.getFromStateId());
            LifecycleState toState = stateMapper.selectById(transition.getToStateId());
            if (fromState != null) {
                transitionVO.setFromStateCode(fromState.getStateCode());
            }
            if (toState != null) {
                transitionVO.setToStateCode(toState.getStateCode());
            }

            transitionVOs.add(transitionVO);
        }
        vo.setTransitions(transitionVOs);

        return vo;
    }
}
