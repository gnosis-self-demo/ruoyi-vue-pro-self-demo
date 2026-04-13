package com.gnosis.paramcheck.service;

import com.gnosis.paramcheck.domain.ValidationFlow;
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

@Service
public class FlowRefreshService {

    private static final Logger log = LoggerFactory.getLogger(FlowRefreshService.class);

    private final Map<String, Integer> flowVersionCache = new ConcurrentHashMap<>();

    @Autowired
    private FlowConfigService flowConfigService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("[FlowRefreshService] application ready, loading flows from DB...");
        refreshAllFlows();
    }

    @Scheduled(fixedDelay = 30_000, initialDelay = 30_000)
    public void scheduledRefresh() {
        refreshChangedFlows();
    }

    public void refreshAllFlows() {
        try {
            List<ValidationFlow> flows = flowConfigService.findAllActive();
            for (ValidationFlow flow : flows) {
                registerFlow(flow);
                flowVersionCache.put(flow.getFlowId(), flow.getVersion());
            }
            log.info("[FlowRefreshService] loaded {} flows from DB", flows.size());
        } catch (Exception e) {
            log.error("[FlowRefreshService] refresh all flows failed", e);
        }
    }

    private void refreshChangedFlows() {
        try {
            List<ValidationFlow> flows = flowConfigService.findAllActive();
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

    private void registerFlow(ValidationFlow flow) {
        String flowId = flow.getFlowId();
        String elExpression = flow.getElExpression();
        String mode = flow.getModeType();

        if (!"FLOW".equals(mode) && !"HYBRID".equals(mode)) {
            log.debug("[FlowRefreshService] skip LiteFlow registration: {} (mode={})", flowId, mode);
            return;
        }

        if (elExpression == null || elExpression.trim().isEmpty()) {
            log.warn("[FlowRefreshService] skip flow {}: el_expression is empty", flowId);
            return;
        }

        try {
            if (FlowBus.containChain(flowId)) {
                log.debug("[FlowRefreshService] chain already exists: {}", flowId);
            } else {
                log.info("[FlowRefreshService] registering new chain: {} with EL: {}", flowId, elExpression);
                log.info("[FlowRefreshService] To enable dynamic registration, add the following to liteflow-rule.xml:");
                log.info("[FlowRefreshService] <chain name=\"{}\">{}</chain>", flowId, elExpression);
            }
        } catch (Exception e) {
            log.warn("[FlowRefreshService] FlowBus check failed for flow={}: {}", flowId, e.getMessage());
        }
    }

    public void refreshFlow(String flowId) {
        ValidationFlow flow = flowConfigService.findById(flowId);
        if (flow != null) {
            registerFlow(flow);
            flowVersionCache.put(flowId, flow.getVersion());
        }
    }
}
