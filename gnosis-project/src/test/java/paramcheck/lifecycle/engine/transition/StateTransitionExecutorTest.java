package paramcheck.lifecycle.engine.transition;

import lifecycle.engine.transition.StateTransitionExecutor;
import lifecycle.engine.transition.TransitionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import lifecycle.domain.LifecycleInstance;
import lifecycle.domain.LifecycleState;
import lifecycle.domain.LifecycleStateTransition;
import lifecycle.dto.TransitionExecutionRequest;
import lifecycle.repository.LifecycleInstanceMapper;
import lifecycle.repository.LifecycleStateMapper;
import lifecycle.repository.LifecycleStateTransitionMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 状态流转执行器测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class StateTransitionExecutorTest {

    @Mock
    private LifecycleStateTransitionMapper transitionMapper;

    @Mock
    private LifecycleStateMapper stateMapper;

    @Mock
    private LifecycleInstanceMapper instanceMapper;

    @InjectMocks
    private StateTransitionExecutor stateTransitionExecutor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 测试有效流转
     */
    @Test
    public void testValidTransition() {
        // 准备测试数据
        TransitionContext context = createTestContext();

        // 创建流转配置
        LifecycleStateTransition transition = new LifecycleStateTransition();
        transition.setId("transition_001");
        transition.setTransitionCode("APPROVE");
        transition.setFromStateId("state_pending");
        transition.setToStateId("state_approved");

        // 创建源状态
        LifecycleState fromState = new LifecycleState();
        fromState.setId("state_pending");
        fromState.setStateCode("PENDING");
        fromState.setStateName("待审批");

        // 创建目标状态
        LifecycleState toState = new LifecycleState();
        toState.setId("state_approved");
        toState.setStateCode("APPROVED");
        toState.setStateName("已审批");

        // 创建业务实例
        LifecycleInstance instance = new LifecycleInstance();
        instance.setId("instance_001");
        instance.setBusinessTypeId("biz_type_001");
        instance.setBusinessInstanceId("biz_instance_001");
        instance.setCurrentStateId("state_pending");
        instance.setCurrentStateCode("PENDING");

        // 配置 Mock 行为
        when(transitionMapper.selectById("transition_001")).thenReturn(transition);
        when(stateMapper.selectById("state_pending")).thenReturn(fromState);
        when(stateMapper.selectById("state_approved")).thenReturn(toState);
        when(instanceMapper.selectList(any(LifecycleInstance.class))).thenReturn(createInstanceList(instance));

        // 执行测试
        StateTransitionExecutor.TransitionResult result = stateTransitionExecutor.execute(context);

        // 验证结果
        assertNotNull("结果不应为空", result);
        assertTrue("流转应该成功", result.isSuccess());
        assertEquals("新状态 ID 应该匹配", "state_approved", result.getNewStateId());
        assertEquals("新状态编码应该匹配", "APPROVED", result.getNewStateCode());
        assertTrue("消息应该包含成功信息", result.getMessage().contains("状态流转成功"));

        // 验证 Mock 调用
        verify(transitionMapper, times(1)).selectById("transition_001");
        verify(stateMapper, times(1)).selectById("state_pending");
        verify(stateMapper, times(1)).selectById("state_approved");
        verify(instanceMapper, times(1)).updateById(any(LifecycleInstance.class));
    }

    /**
     * 测试无效流转 - 流转不存在
     */
    @Test
    public void testInvalidTransition_TransitionNotFound() {
        // 准备测试数据
        TransitionContext context = createTestContext();

        // 配置 Mock 行为 - 流转不存在
        when(transitionMapper.selectById("transition_001")).thenReturn(null);

        // 执行测试
        StateTransitionExecutor.TransitionResult result = stateTransitionExecutor.execute(context);

        // 验证结果
        assertNotNull("结果不应为空", result);
        assertFalse("流转应该失败", result.isSuccess());
        assertEquals("错误码应该匹配", "TRANSITION_NOT_FOUND", result.getErrorCode());
        assertTrue("消息应该包含错误信息", result.getMessage().contains("流转不存在"));

        // 验证 Mock 调用 - 不应该更新实例
        verify(transitionMapper, times(1)).selectById("transition_001");
        verify(stateMapper, never()).selectById(anyString());
        verify(instanceMapper, never()).updateById(any(LifecycleInstance.class));
    }

    /**
     * 测试无效流转 - 源状态不存在
     */
    @Test
    public void testInvalidTransition_FromStateNotFound() {
        // 准备测试数据
        TransitionContext context = createTestContext();

        LifecycleStateTransition transition = new LifecycleStateTransition();
        transition.setId("transition_001");
        transition.setTransitionCode("APPROVE");
        transition.setFromStateId("state_pending");
        transition.setToStateId("state_approved");

        // 配置 Mock 行为
        when(transitionMapper.selectById("transition_001")).thenReturn(transition);
        when(stateMapper.selectById("state_pending")).thenReturn(null);

        // 执行测试
        StateTransitionExecutor.TransitionResult result = stateTransitionExecutor.execute(context);

        // 验证结果
        assertNotNull("结果不应为空", result);
        assertFalse("流转应该失败", result.isSuccess());
        assertEquals("错误码应该匹配", "FROM_STATE_NOT_FOUND", result.getErrorCode());

        // 验证 Mock 调用
        verify(transitionMapper, times(1)).selectById("transition_001");
        verify(stateMapper, times(1)).selectById("state_pending");
        verify(stateMapper, never()).selectById("state_approved");
    }

    /**
     * 测试无效流转 - 目标状态不存在
     */
    @Test
    public void testInvalidTransition_ToStateNotFound() {
        // 准备测试数据
        TransitionContext context = createTestContext();

        LifecycleStateTransition transition = new LifecycleStateTransition();
        transition.setId("transition_001");
        transition.setTransitionCode("APPROVE");
        transition.setFromStateId("state_pending");
        transition.setToStateId("state_approved");

        LifecycleState fromState = new LifecycleState();
        fromState.setId("state_pending");
        fromState.setStateCode("PENDING");

        // 配置 Mock 行为
        when(transitionMapper.selectById("transition_001")).thenReturn(transition);
        when(stateMapper.selectById("state_pending")).thenReturn(fromState);
        when(stateMapper.selectById("state_approved")).thenReturn(null);

        // 执行测试
        StateTransitionExecutor.TransitionResult result = stateTransitionExecutor.execute(context);

        // 验证结果
        assertNotNull("结果不应为空", result);
        assertFalse("流转应该失败", result.isSuccess());
        assertEquals("错误码应该匹配", "TO_STATE_NOT_FOUND", result.getErrorCode());

        // 验证 Mock 调用
        verify(transitionMapper, times(1)).selectById("transition_001");
        verify(stateMapper, times(1)).selectById("state_pending");
        verify(stateMapper, times(1)).selectById("state_approved");
    }

    /**
     * 测试无效流转 - 实例状态不匹配
     */
    @Test
    public void testInvalidTransition_StateMismatch() {
        // 准备测试数据
        TransitionContext context = createTestContext();

        LifecycleStateTransition transition = new LifecycleStateTransition();
        transition.setId("transition_001");
        transition.setTransitionCode("APPROVE");
        transition.setFromStateId("state_pending");
        transition.setToStateId("state_approved");

        LifecycleState fromState = new LifecycleState();
        fromState.setId("state_pending");
        fromState.setStateCode("PENDING");

        LifecycleState toState = new LifecycleState();
        toState.setId("state_approved");
        toState.setStateCode("APPROVED");

        // 创建业务实例 - 当前状态与流转源状态不匹配
        LifecycleInstance instance = new LifecycleInstance();
        instance.setId("instance_001");
        instance.setBusinessTypeId("biz_type_001");
        instance.setBusinessInstanceId("biz_instance_001");
        instance.setCurrentStateId("state_draft"); // 不匹配的状态
        instance.setCurrentStateCode("DRAFT");

        // 配置 Mock 行为
        when(transitionMapper.selectById("transition_001")).thenReturn(transition);
        when(stateMapper.selectById("state_pending")).thenReturn(fromState);
        when(stateMapper.selectById("state_approved")).thenReturn(toState);
        when(instanceMapper.selectList(any(LifecycleInstance.class))).thenReturn(createInstanceList(instance));

        // 执行测试
        StateTransitionExecutor.TransitionResult result = stateTransitionExecutor.execute(context);

        // 验证结果
        assertNotNull("结果不应为空", result);
        assertFalse("流转应该失败", result.isSuccess());
        assertEquals("错误码应该匹配", "STATE_MISMATCH", result.getErrorCode());
        assertTrue("消息应该包含状态不匹配信息", result.getMessage().contains("当前状态不匹配"));

        // 验证 Mock 调用
        verify(transitionMapper, times(1)).selectById("transition_001");
        verify(stateMapper, times(1)).selectById("state_pending");
        verify(stateMapper, times(1)).selectById("state_approved");
        verify(instanceMapper, never()).updateById(any(LifecycleInstance.class));
    }

    /**
     * 测试带追踪的流转
     */
    @Test
    public void testTransitionWithTrace() {
        // 准备测试数据
        TransitionContext context = createTestContext();
        context.setRemark("测试流转 - 带追踪");
        context.setOperatorId("user_123");

        Map<String, Object> transitionData = new HashMap<>();
        transitionData.put("approveComment", "同意");
        transitionData.put("approveTime", new Date());
        context.setTransitionData(transitionData);

        // 创建流转配置
        LifecycleStateTransition transition = new LifecycleStateTransition();
        transition.setId("transition_001");
        transition.setTransitionCode("APPROVE");
        transition.setFromStateId("state_pending");
        transition.setToStateId("state_approved");

        // 创建源状态
        LifecycleState fromState = new LifecycleState();
        fromState.setId("state_pending");
        fromState.setStateCode("PENDING");
        fromState.setStateName("待审批");

        // 创建目标状态
        LifecycleState toState = new LifecycleState();
        toState.setId("state_approved");
        toState.setStateCode("APPROVED");
        toState.setStateName("已审批");

        // 创建业务实例
        LifecycleInstance instance = new LifecycleInstance();
        instance.setId("instance_001");
        instance.setBusinessTypeId("biz_type_001");
        instance.setBusinessInstanceId("biz_instance_001");
        instance.setCurrentStateId("state_pending");
        instance.setCurrentStateCode("PENDING");

        // 配置 Mock 行为
        when(transitionMapper.selectById("transition_001")).thenReturn(transition);
        when(stateMapper.selectById("state_pending")).thenReturn(fromState);
        when(stateMapper.selectById("state_approved")).thenReturn(toState);
        when(instanceMapper.selectList(any(LifecycleInstance.class))).thenReturn(createInstanceList(instance));

        // 执行测试
        StateTransitionExecutor.TransitionResult result = stateTransitionExecutor.execute(context);

        // 验证结果
        assertNotNull("结果不应为空", result);
        assertTrue("流转应该成功", result.isSuccess());
        assertEquals("新状态 ID 应该匹配", "state_approved", result.getNewStateId());

        // 验证实例更新 - 应该包含操作人信息
        // 注意：实际项目中应该验证 instanceMapper.updateById 的参数
        verify(instanceMapper, times(1)).updateById(any(LifecycleInstance.class));
    }

    /**
     * 测试从请求构建上下文
     */
    @Test
    public void testBuildContext() {
        // 准备测试数据
        TransitionExecutionRequest request = new TransitionExecutionRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setBusinessInstanceId("biz_instance_001");
        request.setTransitionId("transition_001");
        request.setTransitionCode("APPROVE");
        request.setFromStateId("state_pending");
        request.setFromStateCode("PENDING");
        request.setToStateId("state_approved");
        request.setToStateCode("APPROVED");
        request.setOperatorId("user_123");
        request.setRemark("测试流转");

        Map<String, Object> data = new HashMap<>();
        data.put("field1", "value1");
        request.setTransitionData(data);

        // 执行测试
        TransitionContext context = stateTransitionExecutor.buildContext(request);

        // 验证结果
        assertNotNull("上下文不应为空", context);
        assertEquals("业务类型 ID 应该匹配", "biz_type_001", context.getBusinessTypeId());
        assertEquals("业务实例 ID 应该匹配", "biz_instance_001", context.getBusinessInstanceId());
        assertEquals("流转 ID 应该匹配", "transition_001", context.getTransitionId());
        assertEquals("流转编码应该匹配", "APPROVE", context.getTransitionCode());
        assertEquals("源状态 ID 应该匹配", "state_pending", context.getFromStateId());
        assertEquals("目标状态 ID 应该匹配", "state_approved", context.getToStateId());
        assertEquals("操作人 ID 应该匹配", "user_123", context.getOperatorId());
        assertEquals("备注应该匹配", "测试流转", context.getRemark());
        assertNotNull("流转数据不应为空", context.getTransitionData());
        assertEquals("流转数据 field1 应该匹配", "value1", context.getTransitionData().get("field1"));
    }

    /**
     * 创建测试上下文
     */
    private TransitionContext createTestContext() {
        TransitionContext context = new TransitionContext();
        context.setBusinessTypeId("biz_type_001");
        context.setBusinessInstanceId("biz_instance_001");
        context.setTransitionId("transition_001");
        context.setTransitionCode("APPROVE");
        context.setFromStateId("state_pending");
        context.setFromStateCode("PENDING");
        context.setToStateId("state_approved");
        context.setToStateCode("APPROVED");
        context.setOperatorId("user_123");
        context.setRemark("测试流转");
        return context;
    }

    /**
     * 创建实例列表
     */
    private List<LifecycleInstance> createInstanceList(LifecycleInstance... instances) {
        List<LifecycleInstance> list = new ArrayList<>();
        for (LifecycleInstance instance : instances) {
            list.add(instance);
        }
        return list;
    }
}
