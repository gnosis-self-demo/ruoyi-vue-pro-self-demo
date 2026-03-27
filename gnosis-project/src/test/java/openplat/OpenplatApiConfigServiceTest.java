package openplat;

import openplat.domain.OpenplatApiConfig;
import openplat.service.OpenplatApiConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 开放平台 API 配置服务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenplatApiConfigServiceTest {
    
    @Autowired
    private OpenplatApiConfigService apiConfigService;
    
    @Test
    public void testSave() {
        OpenplatApiConfig apiConfig = new OpenplatApiConfig();
        apiConfig.setApiCode("TEST_API");
        apiConfig.setApiName("测试接口");
        apiConfig.setApiPath("/api/test");
        apiConfig.setApiMethod("POST");
        apiConfig.setNeedAuth(true);
        apiConfig.setNeedTimestamp(true);
        apiConfig.setNeedNonce(true);
        apiConfig.setRateLimit(1000);
        apiConfig.setStatus("ENABLED");
        apiConfig.setCreateUserId("test");
        apiConfig.setUpdateUserId("test");
        
        int result = apiConfigService.save(apiConfig);
        assert result > 0;
    }
    
    @Test
    public void testGetByApiCode() {
        OpenplatApiConfig apiConfig = apiConfigService.getByApiCode("ORDER_CREATE");
        assert apiConfig != null;
        assert "ORDER_CREATE".equals(apiConfig.getApiCode());
    }
    
    @Test
    public void testGetByApiPath() {
        OpenplatApiConfig apiConfig = apiConfigService.getByApiPath("/api/v1/order/create");
        assert apiConfig != null;
        assert "/api/v1/order/create".equals(apiConfig.getApiPath());
    }
    
    @Test
    public void testList() {
        OpenplatApiConfig query = new OpenplatApiConfig();
        query.setApiMethod("POST");
        List<OpenplatApiConfig> list = apiConfigService.list(query);
        assert list != null;
    }
}
