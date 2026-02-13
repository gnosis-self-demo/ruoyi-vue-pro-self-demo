package gnosis.sample.dynamic.liteflow.component;

import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.slot.DefaultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 替换方法B逻辑的LiteFlow组件
 */
@Component("replaceMethodB")
public class ReplaceMethodBComponent extends NodeComponent {
    
    private static final Logger log = LoggerFactory.getLogger(ReplaceMethodBComponent.class);
    
    @Override
    public void process() throws Exception {
        DefaultContext context = this.getFirstContextBean();
        
        // 获取原始参数
        Object[] originalArgs = context.getData("originalArgs");
        String input = (String) originalArgs[0];
        
        log.info("ReplaceMethodBComponent 处理输入: {}", input);
        
        // 执行新的业务逻辑（替换原方法B的逻辑）
        String result = "LiteFlow组件处理结果: " + input.toUpperCase();
        
        // 设置结果
        context.setData("result", result);
    }
}