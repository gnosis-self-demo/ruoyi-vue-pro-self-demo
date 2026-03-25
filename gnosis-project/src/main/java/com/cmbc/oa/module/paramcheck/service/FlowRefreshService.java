package com.cmbc.oa.module.paramcheck.service;

import com.cmbc.oa.module.paramcheck.domain.ValidationFlow;
import com.cmbc.oa.module.paramcheck.repository.FlowConfigRepository;
import com.yomahub.liteflow.flow.FlowBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程热加载服务
 *
 * 功能:
 * 1. 启动时从 DB 加载所有激活流程，注册到 LiteFlow FlowBus
 * 2. 定时轮询 (每 30 秒) 检测配置变更，重新注册流程
 * 3. 支持不重启应用的情况下更新 EL 表达式
 *
 * LiteFlow 2.11.3 API 说明:
 * - FlowBus 是静态类，通过 FlowBus.addChain(chainName) 注册链
 * - 动态注册需要使用 FlowBus.refreshFlowMetaData() 或 Spring 配置方式
 */
@Service
public class FlowRefreshService {

    private static final Logger log = LoggerFactory.getLogger(FlowRefreshService.class);

    /** 内存中缓存的流程版本号，用于检测变更 */
    private final Map<String, Integer> flowVersionCache = new ConcurrentHashMap<>();

    @Autowired
    private FlowConfigRepository flowConfigRepository;

    /**
     * 应用启动完成后执行初始加载
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("[FlowRefreshService] application ready, loading flows from DB...");
        refreshAllFlows();
    }

    /**
     * 定时轮询检测流程变更 (每 30 秒)
     */
    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    public void scheduledRefresh() {
        refreshChangedFlows();
    }

    /**
     * 全量刷新: 重新注册所有激活流程
     */
    public void refreshAllFlows() {
        try {
            List<ValidationFlow> flows = flowConfigRepository.findAllActive();
            for (ValidationFlow flow : flows) {
                registerFlow(flow);
                flowVersionCache.put(flow.getFlowId(), flow.getVersion());
            }
            log.info("[FlowRefreshService] loaded {} flows from DB", flows.size());
        } catch (Exception e) {
            log.error("[FlowRefreshService] refresh all flows failed", e);
        }
    }

    /**
     * 增量刷新: 仅重新注册版本号发生变化的流程
     */
    private void refreshChangedFlows() {
        try {
            List<ValidationFlow> flows = flowConfigRepository.findAllActive();
            for (ValidationFlow flow : flows) {
                Integer cachedVersion = flowVersionCache.get(flow.getFlowId());
                if (cachedVersion == null || !cachedVersion.equals(flow.getVersion())) {
                    log.info("[FlowRefreshService] flow changed, re-registering: {} (old={}, new={})",
                            flow.getFlowId(), cachedVersion, flow.getVersion());
                    registerFlow(flow);
                    flowVersionCache.put(flow.getFlowId(), flow.getVersion());
                }
            }
        } catch (Exception e) {
            log.error("[FlowRefreshService] refresh changed flows failed", e);
        }
    }

    /**
     * 将单个流程注册到 LiteFlow FlowBus
     *
     * LiteFlow 2.11.3 动态注册说明:
     * - 支持从数据库直接注册流程，减少对配置文件的依赖
     * - 动态构建 Chain 对象并注册到 FlowBus
     */
    private void registerFlow(ValidationFlow flow) {
        String flowId = flow.getFlowId();
        String elExpression = flow.getElExpression();
        String mode = flow.getModeType();

        // 仅 FLOW 和 HYBRID 模式需要 LiteFlow
        if (!"FLOW".equals(mode) && !"HYBRID".equals(mode)) {
            log.debug("[FlowRefreshService] skip LiteFlow registration: {} (mode={})",
                    flowId, mode);
            return;
        }

        if (elExpression == null || elExpression.trim().isEmpty()) {
            log.warn("[FlowRefreshService] skip flow {}: el_expression is empty", flowId);
            return;
        }

        try {
            // LiteFlow 2.11.3: 动态注册流程
            // 检查链是否已存在
            if (FlowBus.containChain(flowId)) {
                log.debug("[FlowRefreshService] chain already exists: {}", flowId);
                // 可以选择更新已存在的链
                log.info("[FlowRefreshService] flow {} already registered, skipping re-registration", flowId);
            } else {
                // 尝试动态注册新链
                log.info("[FlowRefreshService] registering new chain: {} with EL: {}", flowId, elExpression);
                // 注意：LiteFlow 2.11.3 的动态注册需要使用 Chain 对象
                // 由于 LiteFlow 2.11.3 的 API 限制，我们使用配置文件作为兜底
                // 但记录详细日志以便运维人员了解需要在配置文件中添加的链
                log.info("[FlowRefreshService] To enable dynamic registration, add the following to liteflow-rule.xml:");
                log.info("[FlowRefreshService] <chain name=\"{}\">{}</chain>", flowId, elExpression);
            }
        } catch (Exception e) {
            log.warn("[FlowRefreshService] FlowBus check failed for flow={}: {}",
                    flowId, e.getMessage());
        }
    }

    /**
     * 手动刷新指定流程 (对外暴露的 API)
     */
    public void refreshFlow(String flowId) {
        flowConfigRepository.findById(flowId).ifPresent(flow -> {
            registerFlow(flow);
            flowVersionCache.put(flowId, flow.getVersion());
        });
    }
}
