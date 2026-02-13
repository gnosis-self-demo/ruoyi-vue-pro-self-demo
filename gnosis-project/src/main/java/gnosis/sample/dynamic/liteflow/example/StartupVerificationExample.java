package gnosis.sample.dynamic.liteflow.example;

import gnosis.sample.dynamic.liteflow.entity.LiteFlowRule;
import gnosis.sample.dynamic.liteflow.entity.MethodInterceptConfig;
import gnosis.sample.dynamic.liteflow.service.LiteFlowRuleService;
import gnosis.sample.dynamic.liteflow.service.MethodInterceptConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 功能启动验证示例
 * 应用启动后自动创建测试配置并验证功能
 */
@Component
public class StartupVerificationExample implements CommandLineRunner {
    
    @Autowired
    private LiteFlowRuleService liteFlowRuleService;
    
    @Autowired
    private MethodInterceptConfigService methodInterceptConfigService;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== LiteFlow动态切面功能启动验证开始 ===");
        
        try {
            // 1. 创建测试规则
            LiteFlowRule testRule = new LiteFlowRule();
            testRule.setChainName("startupTestChain");
            testRule.setRuleContent("return \"LiteFlow启动验证成功: \" + originalArgs[0];");
            testRule.setRuleType("script");
            testRule.setDescription("启动验证测试规则");
            liteFlowRuleService.saveRule(testRule);
            System.out.println("✓ 测试规则创建成功");
            
            // 2. 创建方法拦截配置
            MethodInterceptConfig interceptConfig = new MethodInterceptConfig();
            interceptConfig.setTargetClass("gnosis.sample.dynamic.liteflow.example.ExampleBusinessService");
            interceptConfig.setTargetMethod("methodB");
            interceptConfig.setChainName("startupTestChain");
            interceptConfig.setDescription("启动验证测试拦截配置");
            methodInterceptConfigService.saveConfig(interceptConfig);
            System.out.println("✓ 方法拦截配置创建成功");
            
            // 3. 刷新缓存
            methodInterceptConfigService.refreshCache();
            System.out.println("✓ 缓存刷新成功");
            
            // 4. 验证功能
            ExampleBusinessService businessService = new ExampleBusinessService();
            String result = businessService.methodA("StartupTest");
            System.out.println("✓ 功能验证结果: " + result);
            
            if (result.contains("LiteFlow启动验证成功")) {
                System.out.println("✅ LiteFlow动态切面功能启动验证通过！");
            } else {
                System.out.println("❌ LiteFlow动态切面功能启动验证失败！");
            }
            
        } catch (Exception e) {
            System.err.println("❌ LiteFlow动态切面功能启动验证异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== LiteFlow动态切面功能启动验证结束 ===");
    }
}