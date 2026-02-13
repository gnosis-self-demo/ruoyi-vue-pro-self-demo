package gnosis.sample.dynamic.liteflow.component;

import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.slot.DefaultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 示例LiteFlow组件
 */
@Component("sampleComponent")
public class SampleComponent extends NodeComponent {
    
    private static final Logger log = LoggerFactory.getLogger(SampleComponent.class);
    
    @Override
    public void process() throws Exception {
        DefaultContext context = this.getFirstContextBean();
        String input = context.getData("input");
        log.info("SampleComponent processing input: {}", input);
        
        // 处理逻辑
        String result = "Processed: " + input;
        context.setData("result", result);
    }
}