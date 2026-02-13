package gnosis.sample.dynamic.liteflow.example;

import org.springframework.stereotype.Service;

/**
 * 示例业务服务类，用于演示动态切面功能
 */
@Service
public class ExampleBusinessService {
    
    /**
     * 方法A - 这个方法会被配置为触发LiteFlow流程
     */
    public String methodA(String input) {
        System.out.println("执行原方法A: " + input);
        // 调用方法B
        String result = methodB(input);
        return "方法A结果: " + result;
    }
    
    /**
     * 方法B - 这个方法的逻辑会被LiteFlow组件替换
     */
    public String methodB(String input) {
        System.out.println("执行原方法B: " + input);
        return "原方法B处理结果: " + input;
    }
}