package com.gnosis.lifecycle.service;

import com.gnosis.lifecycle.domain.LifecycleEvent;
import com.gnosis.lifecycle.domain.LifecycleRouteRule;
import com.gnosis.lifecycle.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gnosis.lifecycle.adapter.StandardEvent;
import com.gnosis.lifecycle.adapter.WorkflowEventAdapter;

import com.gnosis.lifecycle.engine.router.ConditionRouterEngine;
import com.gnosis.lifecycle.engine.router.RouteMatchResult;
import com.gnosis.lifecycle.engine.sandbox.RuleTestSandbox;
import com.gnosis.lifecycle.engine.sandbox.TestSimulationResult;
import com.gnosis.lifecycle.engine.transition.StateTransitionExecutor;
import com.gnosis.lifecycle.engine.transition.TransitionContext;
import com.gnosis.lifecycle.engine.transition.TransitionContext.TransitionResult;
import com.gnosis.lifecycle.repository.LifecycleEventMapper;
import com.gnosis.lifecycle.repository.LifecycleRouteRuleMapper;

import java.util.*;

/**
 * 事件服务实现类
 */
@Service
public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private WorkflowEventAdapter workflowEventAdapter;

    @Autowired
    private ConditionRouterEngine conditionRouterEngine;

    @Autowired
    private RuleTestSandbox ruleTestSandbox;

    @Autowired
    private StateTransitionExecutor stateTransitionExecutor;

    @Autowired
    private LifecycleEventMapper eventMapper;

    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EventVO submitEvent(EventSubmitRequest request) {
        log.info("[EventService] 提交事件：businessTypeId={}, eventType={}, businessInstanceId={}", 
                request.getBusinessTypeId(), request.getEventType(), request.getBusinessInstanceId());

        // 1. 构建标准事件
        StandardEvent event = buildStandardEvent(request);

        // 2. 验证事件
        if (!workflowEventAdapter.validateEvent(event)) {
            throw new IllegalArgumentException("事件验证失败");
        }

        // 3. 查询路由规则
        List<LifecycleRouteRule> rules = queryRouteRules(request.getBusinessTypeId(), request.getEventType());

        // 4. 执行路由匹配
        List<RouteMatchResult> matchResults = conditionRouterEngine.match(event, rules);

        // 5. 获取首条匹配的规则（优先级最高）
        RouteMatchResult matchedRule = matchResults.stream()
                .filter(RouteMatchResult::isMatched)
                .findFirst()
                .orElse(null);

        // 6. 保存事件
        LifecycleEvent savedEvent = saveEvent(request, event);

        // 7. 如果有匹配的规则，执行相应操作
        if (matchedRule != null) {
            log.info("[EventService] 匹配到规则：ruleId={}, ruleName={}", 
                    matchedRule.getRuleId(), matchedRule.getRuleName());
            // 这里可以触发后续的动作，如状态流转、下游调用等
        }

        return convertToVO(savedEvent);
    }

    @Override
    public EventVO getById(String id) {
        log.debug("[EventService] 查询事件详情：id={}", id);

        LifecycleEvent event = eventMapper.selectById(id);
        if (event == null) {
            throw new IllegalArgumentException("事件不存在：" + id);
        }

        return convertToVO(event);
    }

    @Override
    public List<EventVO> list(String businessTypeId, String eventType, int pageNum, int pageSize) {
        log.debug("[EventService] 查询事件列表：businessTypeId={}, eventType={}, pageNum={}, pageSize={}", 
                businessTypeId, eventType, pageNum, pageSize);

        // TODO: 实现分页查询
        return new ArrayList<>();
    }

    @Override
    public RouteTestResultVO testRoute(RouteTestRequest request) {
        log.info("[EventService] 测试路由规则：businessTypeId={}, eventType={}", 
                request.getBusinessTypeId(), request.getEventType());

        long startTime = System.currentTimeMillis();

        // 1. 构建测试事件
        StandardEvent testEvent = buildTestEvent(request);

        // 2. 查询路由规则
        List<LifecycleRouteRule> rules;
        if (request.getRuleId() != null) {
            // 测试指定规则
            LifecycleRouteRule rule = routeRuleMapper.selectById(request.getRuleId());
            if (rule == null) {
                throw new IllegalArgumentException("规则不存在：" + request.getRuleId());
            }
            rules = Collections.singletonList(rule);
        } else {
            // 测试所有规则
            rules = queryRouteRules(request.getBusinessTypeId(), request.getEventType());
        }

        // 3. 执行沙箱模拟测试
        TestSimulationResult simulationResult = ruleTestSandbox.simulate(testEvent, rules);

        // 4. 转换为 VO
        RouteTestResultVO resultVO = convertToTestResultVO(simulationResult, request);

        long endTime = System.currentTimeMillis();
        resultVO.setExecutionTimeMs(endTime - startTime);

        log.info("[EventService] 路由测试完成，匹配：{}", resultVO.isMatched());
        return resultVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> executeTransition(TransitionExecutionRequest request) {
        log.info("[EventService] 执行状态流转：businessInstanceId={}, transitionCode={}", 
                request.getBusinessInstanceId(), request.getTransitionCode());

        // 1. 构建流转上下文
        TransitionContext context = stateTransitionExecutor.buildContext(request);

        // 2. 执行流转
        TransitionResult result = stateTransitionExecutor.execute(context);

        // 3. 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        if (!result.isSuccess()) {
            response.put("errorCode", result.getErrorCode());
        }
        if (result.getNewStateId() != null) {
            response.put("newStateId", result.getNewStateId());
            response.put("newStateCode", result.getNewStateCode());
        }

        return response;
    }

    @Override
    public Map<String, Object> getMetadataDictionary() {
        WorkflowEventAdapter.EventMetadataDictionary dictionary = workflowEventAdapter.getMetadataDictionary();
        
        Map<String, Object> result = new HashMap<>();
        result.put("fields", dictionary.getFields());
        result.put("total", dictionary.getFields().length);
        
        return result;
    }

    /**
     * 构建标准事件
     */
    private StandardEvent buildStandardEvent(EventSubmitRequest request) {
        StandardEvent event = new StandardEvent();
        event.setEventId(java.util.UUID.randomUUID().toString().replace("-", ""));
        event.setBusinessTypeId(request.getBusinessTypeId());
        event.setEventType(request.getEventType());
        event.setBusinessInstanceId(request.getBusinessInstanceId());
        event.setCurrentStateCode(request.getCurrentStateCode());
        event.setEventData(request.getEventData());
        event.setMetadata(request.getMetadata());
        event.setOperatorId(request.getOperatorId());
        event.setRemark(request.getRemark());
        event.setEventTime(new Date());
        return event;
    }

    /**
     * 构建测试事件
     */
    private StandardEvent buildTestEvent(RouteTestRequest request) {
        StandardEvent event = new StandardEvent();
        event.setEventId("TEST_" + System.currentTimeMillis());
        event.setBusinessTypeId(request.getBusinessTypeId());
        event.setEventType(request.getEventType());
        event.setBusinessInstanceId("TEST_INSTANCE");
        event.setCurrentStateCode(request.getCurrentStateCode());
        event.setEventData(request.getTestData());
        event.setMetadata(request.getTestData()); // 测试数据也作为元数据
        event.setEventTime(new Date());
        return event;
    }

    /**
     * 查询路由规则
     */
    private List<LifecycleRouteRule> queryRouteRules(String businessTypeId, String eventType) {
        LifecycleRouteRule query = new LifecycleRouteRule();
        query.setBusinessTypeId(businessTypeId);
        query.setEventType(eventType);
        query.setIsActive(1); // 只查询启用的规则

        List<LifecycleRouteRule> rules = routeRuleMapper.selectList(query);
        
        // 按优先级降序排序
        rules.sort((r1, r2) -> r2.getPriority().compareTo(r1.getPriority()));
        
        return rules;
    }

    /**
     * 保存事件
     */
    private LifecycleEvent saveEvent(EventSubmitRequest request, StandardEvent event) {
        LifecycleEvent lifecycleEvent = new LifecycleEvent();
        lifecycleEvent.setId(event.getEventId());
        lifecycleEvent.setBusinessTypeId(request.getBusinessTypeId());
        lifecycleEvent.setEventType(request.getEventType());
        lifecycleEvent.setBusinessInstanceId(request.getBusinessInstanceId());
        lifecycleEvent.setEventData(com.alibaba.fastjson.JSON.toJSONString(request.getEventData()));
        lifecycleEvent.setMetadata(com.alibaba.fastjson.JSON.toJSONString(request.getMetadata()));
        lifecycleEvent.setOperatorId(request.getOperatorId());
        lifecycleEvent.setRemark(request.getRemark());
        lifecycleEvent.setProcessStatus("PENDING");
        lifecycleEvent.setCreateUserId(request.getOperatorId() != null ? request.getOperatorId() : "system");
        lifecycleEvent.setUpdateUserId(request.getOperatorId() != null ? request.getOperatorId() : "system");
        lifecycleEvent.setCreateTime(new Date());
        lifecycleEvent.setUpdateTime(new Date());

        eventMapper.insert(lifecycleEvent);
        return lifecycleEvent;
    }

    /**
     * 转换为 VO
     */
    private EventVO convertToVO(LifecycleEvent event) {
        EventVO vo = new EventVO();
        BeanUtils.copyProperties(event, vo);
        
        // 解析 JSON 字段
        if (event.getEventData() != null) {
            try {
                vo.setEventData(com.alibaba.fastjson.JSON.parseObject(event.getEventData(), Map.class));
            } catch (Exception e) {
                log.warn("[EventService] 解析 eventData 失败", e);
            }
        }
        if (event.getMetadata() != null) {
            try {
                vo.setMetadata(com.alibaba.fastjson.JSON.parseObject(event.getMetadata(), Map.class));
            } catch (Exception e) {
                log.warn("[EventService] 解析 metadata 失败", e);
            }
        }
        
        return vo;
    }

    /**
     * 转换为测试结果 VO
     */
    private RouteTestResultVO convertToTestResultVO(TestSimulationResult simulation, RouteTestRequest request) {
        RouteTestResultVO vo = new RouteTestResultVO();
        vo.setMatched(simulation.getMatchedCount() > 0);
        vo.setMessage(simulation.getMessage());
        vo.setExecutionTimeMs(simulation.getExecutionTimeMs());
        vo.setFinalRouteResult(simulation.getFinalRouteResult());

        // 转换匹配的规则
        List<RouteTestResultVO.MatchedRuleVO> matchedVOs = new ArrayList<>();
        for (TestSimulationResult.MatchedRuleDetail detail : simulation.getMatchedRules()) {
            RouteTestResultVO.MatchedRuleVO matchedVO = new RouteTestResultVO.MatchedRuleVO();
            matchedVO.setRuleId(detail.getRuleId());
            matchedVO.setRuleName(detail.getRuleName());
            matchedVO.setPriority(detail.getPriority());
            matchedVO.setConditionExpression(detail.getConditionExpression());
            matchedVO.setMatchDetail(detail.getMatchDetail());
            matchedVOs.add(matchedVO);
        }
        vo.setMatchedRules(matchedVOs);

        // 转换未匹配的规则
        List<RouteTestResultVO.UnmatchedRuleVO> unmatchedVOs = new ArrayList<>();
        for (TestSimulationResult.UnmatchedRuleDetail detail : simulation.getUnmatchedRules()) {
            RouteTestResultVO.UnmatchedRuleVO unmatchedVO = new RouteTestResultVO.UnmatchedRuleVO();
            unmatchedVO.setRuleId(detail.getRuleId());
            unmatchedVO.setRuleName(detail.getRuleName());
            unmatchedVO.setPriority(detail.getPriority());
            unmatchedVO.setConditionExpression(detail.getConditionExpression());
            unmatchedVO.setUnmatchReason(detail.getUnmatchReason());
            unmatchedVOs.add(unmatchedVO);
        }
        vo.setUnmatchedRules(unmatchedVOs);

        return vo;
    }
}
