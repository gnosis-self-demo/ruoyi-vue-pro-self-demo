package gnosis.sample.dynamic.liteflow.component;

import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.slot.DefaultContext;
import org.springframework.stereotype.Component;

/**
 * 替换方法B的组件
 */
@Component("replaceMethodBComponent")
public class ReplaceMethodBComponent extends NodeComponent {

    @Override
    public void process() throws Exception {
        System.out.println("Executing ReplaceMethodBComponent - Replacing methodB logic");
        DefaultContext context = this.getSlot().getFirstContextBean();
        
        // 获取上下文中的参数
        Object[] originalArgs = context.getData("originalArgs");
        String className = context.getData("className");
        String methodName = context.getData("methodName");
        
        System.out.println("Intercepted method: " + className + "." + methodName);
        if (originalArgs != null) {
            System.out.println("Original arguments: " + java.util.Arrays.toString(originalArgs));
        }
        
        // 设置替换后的结果
        context.setData("result", "Replaced result from ReplaceMethodBComponent");
    }
}