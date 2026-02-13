package gnosis.sample.dynamic.liteflow;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.slot.DefaultContext;
import gnosis.sample.dynamic.liteflow.entity.LiteFlowRule;
import gnosis.sample.dynamic.liteflow.service.LiteFlowRuleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * LiteFlow集成测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LiteFlowIntegrationTest {
    
    @Autowired
    private LiteFlowRuleService liteFlowRuleService;
    
    @Autowired
    private FlowExecutor flowExecutor;
    
    @Test
    public void testLiteFlowExecution() {
        // 创建测试规则
        LiteFlowRule rule = new LiteFlowRule();
        rule.setChainName("testChain");
        rule.setRuleContent("<chain name=\"testChain\">\n" +
                           "    <then value=\"sampleComponent\"/>\n" +
                           "</chain>");
        rule.setRuleType("xml");
        rule.setEnabled(true);
        rule.setDescription("Test rule for integration");
        
        // 保存规则
        liteFlowRuleService.saveRule(rule);
        
        // 执行规则
        DefaultContext context = new DefaultContext();
        context.setData("input", "Hello World");
        
        flowExecutor.execute2Resp("testChain", context);
        
        String result = context.getData("result");
        assertEquals("Processed: Hello World", result);
    }
}