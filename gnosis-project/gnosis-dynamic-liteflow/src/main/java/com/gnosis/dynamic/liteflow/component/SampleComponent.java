package com.gnosis.dynamic.liteflow.component;

import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.slot.DefaultContext;
import org.springframework.stereotype.Component;

/**
 * 示例组件
 */
@Component("sampleComponent")
public class SampleComponent extends NodeComponent {

    @Override
    public void process() throws Exception {
        System.out.println("Executing SampleComponent");
        DefaultContext context = this.getSlot().getFirstContextBean();
        
        // 获取上下文中的参数
        Object[] originalArgs = context.getData("originalArgs");
        String className = context.getData("className");
        String methodName = context.getData("methodName");
        
        // 这里可以执行自定义逻辑
        System.out.println("Intercepted method: " + className + "." + methodName);
        if (originalArgs != null) {
            System.out.println("Original arguments: " + java.util.Arrays.toString(originalArgs));
        }
        
        // 设置返回结果到上下文
        context.setData("result", "Result from SampleComponent");
    }
}