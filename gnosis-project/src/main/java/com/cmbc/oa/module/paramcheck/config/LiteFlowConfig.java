package com.cmbc.oa.module.paramcheck.config;

import org.springframework.context.annotation.Configuration;

/**
 * LiteFlow 2.11.3 配置
 *
 * LiteFlow 2.11.3 默认从 classpath:liteflow-rule.xml 加载流程规则。
 * 本模块同时支持从数据库动态加载流程，因此规则文件作为兜底，
 * 运行时热加载的流程通过 FlowRefreshService 注册到 FlowBus。
 */
@Configuration("paramCheckLiteFlowConfig")
public class LiteFlowConfig {

    /**
     * LiteFlow 2.11.3 API 说明:
     * - FlowExecutor: 流程执行器 (在 Service 层注入使用)
     * - FlowBus: 流程元数据管理，动态注册链/节点
     *   导入: com.yomahub.liteflow.flow.FlowBus
     *
     * 注意: 本配置类主要作为配置声明，
     *       实际的 FlowBus 动态注册逻辑在 FlowRefreshService 中实现。
     */
}
