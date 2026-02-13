package gnosis.sample.dynamic.liteflow.web;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.slot.DefaultContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * LiteFlow规则执行测试控制器
 */
@RestController
@RequestMapping("/api/liteflow/execute")
public class LiteFlowExecuteController {
    
    @Autowired
    private FlowExecutor flowExecutor;
    
    /**
     * 执行指定的流程链
     */
    @PostMapping("/{chainName}")
    public String executeChain(@PathVariable String chainName, @RequestBody String input) {
        DefaultContext context = new DefaultContext();
        context.setData("input", input);
        
        try {
            flowExecutor.execute2Resp(chainName, context);
            String result = context.getData("result");
            return "Execution successful. Result: " + result;
        } catch (Exception e) {
            return "Execution failed: " + e.getMessage();
        }
    }
}