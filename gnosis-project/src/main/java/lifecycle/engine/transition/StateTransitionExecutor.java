package lifecycle.engine.transition;

import lifecycle.domain.LifecycleInstance;
import lifecycle.domain.LifecycleState;
import lifecycle.domain.LifecycleStateTransition;
import lifecycle.dto.TransitionExecutionRequest;
import lifecycle.repository.LifecycleInstanceMapper;
import lifecycle.repository.LifecycleStateMapper;
import lifecycle.repository.LifecycleStateTransitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 状态流转执行器
 * 负责执行状态流转操作
 */
@Component
public class StateTransitionExecutor {

    private static final Logger log = LoggerFactory.getLogger(StateTransitionExecutor.class);

    @Autowired
    private LifecycleStateTransitionMapper transitionMapper;

    @Autowired
    private LifecycleStateMapper stateMapper;

    @Autowired
    private LifecycleInstanceMapper instanceMapper;

    /**
     * 执行状态流转
     *
     * @param context 流转上下文
     * @return 流转结果
     */
    @Transactional(rollbackFor = Exception.class)
    public TransitionContext.TransitionResult execute(TransitionContext context) {
        log.info("[StateTransitionExecutor] 执行状态流转：businessInstanceId={}, transitionCode={}", 
                context.getBusinessInstanceId(), context.getTransitionCode());

        try {
            // 1. 校验流转是否存在
            LifecycleStateTransition transition = transitionMapper.selectById(context.getTransitionId());
            if (transition == null) {
                return TransitionContext.TransitionResult.failure("TRANSITION_NOT_FOUND", "流转不存在：" + context.getTransitionId());
            }

            // 2. 校验源状态
            LifecycleState fromState = stateMapper.selectById(transition.getFromStateId());
            if (fromState == null) {
                return TransitionContext.TransitionResult.failure("FROM_STATE_NOT_FOUND", "源状态不存在");
            }

            // 3. 校验目标状态
            LifecycleState toState = stateMapper.selectById(transition.getToStateId());
            if (toState == null) {
                return TransitionContext.TransitionResult.failure("TO_STATE_NOT_FOUND", "目标状态不存在");
            }

            // 4. 校验当前状态是否与源状态匹配
            LifecycleInstance instance = getInstance(context.getBusinessTypeId(), context.getBusinessInstanceId());
            if (instance == null) {
                return TransitionContext.TransitionResult.failure("INSTANCE_NOT_FOUND", "业务实例不存在");
            }

            if (!instance.getCurrentStateId().equals(transition.getFromStateId())) {
                return TransitionContext.TransitionResult.failure("STATE_MISMATCH",
                        "当前状态不匹配，期望：" + fromState.getStateCode() + ", 实际：" + instance.getCurrentStateCode());
            }

            // 5. 执行业务校验（如果有）
            TransitionContext.TransitionResult validation = validate(context, transition);
            if (!validation.isSuccess()) {
                return validation;
            }

            // 6. 更新实例状态
            updateInstanceState(instance, transition, toState, context);

            // 7. 记录流转日志
            logTransition(context, transition, fromState, toState);

            TransitionContext.TransitionResult result = TransitionContext.TransitionResult.success(
                    toState.getId(),
                    toState.getStateCode(),
                    "状态流转成功：" + fromState.getStateCode() + " -> " + toState.getStateCode()
            );

            log.info("[StateTransitionExecutor] 状态流转成功：businessInstanceId={}, from={}, to={}", 
                    context.getBusinessInstanceId(), fromState.getStateCode(), toState.getStateCode());

            return result;

        } catch (Exception e) {
            log.error("[StateTransitionExecutor] 状态流转失败", e);
            return TransitionContext.TransitionResult.failure("TRANSITION_ERROR", "状态流转异常：" + e.getMessage());
        }
    }

    /**
     * 执行业务校验
     */
    protected TransitionContext.TransitionResult validate(TransitionContext context, LifecycleStateTransition transition) {
        log.debug("[StateTransitionExecutor] 执行业务校验");

        // 可以在这里添加自定义的业务校验逻辑
        // 例如：检查条件表达式、权限校验等

        return TransitionContext.TransitionResult.success(null, null, "校验通过");
    }

    /**
     * 更新实例状态
     */
    private void updateInstanceState(LifecycleInstance instance, LifecycleStateTransition transition, 
                                     LifecycleState toState, TransitionContext context) {
        log.debug("[StateTransitionExecutor] 更新实例状态：instanceId={}, toStateId={}", 
                instance.getId(), toState.getId());

        instance.setCurrentStateId(toState.getId());
        instance.setCurrentStateCode(toState.getStateCode());
        instance.setUpdateUserId(context.getOperatorId() != null ? context.getOperatorId() : "system");
        instance.setUpdateTime(new Date());

        instanceMapper.updateById(instance);
    }

    /**
     * 记录流转日志
     */
    private void logTransition(TransitionContext context, LifecycleStateTransition transition, 
                               LifecycleState fromState, LifecycleState toState) {
        log.info("[StateTransitionExecutor] 状态流转日志：businessInstanceId={}, transition={}, from={}, to={}, operator={}", 
                context.getBusinessInstanceId(), 
                transition.getTransitionCode(),
                fromState.getStateCode(),
                toState.getStateCode(),
                context.getOperatorId());

        // 实际项目中应该写入流转日志表 lifecycle_instance_trace
    }

    /**
     * 获取业务实例
     */
    private LifecycleInstance getInstance(String businessTypeId, String businessInstanceId) {
        // 这里简化处理，实际应该从数据库查询
        // 由于 LifecycleInstanceMapper 可能还没有这个方法，暂时返回 null
        List<LifecycleInstance> instances = instanceMapper.selectList(new LifecycleInstance());
        for (LifecycleInstance instance : instances) {
            if (instance.getBusinessInstanceId().equals(businessInstanceId)) {
                return instance;
            }
        }
        return null;
    }

    /**
     * 从请求构建上下文
     */
    public TransitionContext buildContext(TransitionExecutionRequest request) {
        TransitionContext context = new TransitionContext();
        context.setBusinessTypeId(request.getBusinessTypeId());
        context.setBusinessInstanceId(request.getBusinessInstanceId());
        context.setTransitionId(request.getTransitionId());
        context.setTransitionCode(request.getTransitionCode());
        context.setFromStateId(request.getFromStateId());
        context.setFromStateCode(request.getFromStateCode());
        context.setToStateId(request.getToStateId());
        context.setToStateCode(request.getToStateCode());
        context.setTransitionData(request.getTransitionData());
        context.setOperatorId(request.getOperatorId());
        context.setRemark(request.getRemark());
        return context;
    }
}
