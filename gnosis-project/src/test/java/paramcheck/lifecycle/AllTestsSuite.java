package paramcheck.lifecycle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import paramcheck.lifecycle.adapter.WorkflowEventAdapterTest;
import paramcheck.lifecycle.api.EntryApiTest;
import paramcheck.lifecycle.api.EventApiTest;
import paramcheck.lifecycle.api.GovernanceApiTest;
import paramcheck.lifecycle.entry.EntryServiceTest;
import paramcheck.lifecycle.engine.router.ConditionRouterEngineTest;
import paramcheck.lifecycle.engine.transition.StateTransitionExecutorTest;
import paramcheck.lifecycle.performance.PerformanceTest;
import paramcheck.lifecycle.service.TraceServiceTest;

/**
 * 业务生命周期管理组件 - 完整测试套件
 * 包含所有单元测试、集成测试、API 测试和性能测试
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // 单元测试
    EntryServiceTest.class,
    ConditionRouterEngineTest.class,
    WorkflowEventAdapterTest.class,
    StateTransitionExecutorTest.class,
    TraceServiceTest.class,
    
    // API 测试
    EntryApiTest.class,
    EventApiTest.class,
    GovernanceApiTest.class,
    
    // 性能测试
    PerformanceTest.class
})
public class AllTestsSuite {
    // 测试套件类，无需额外代码
}
