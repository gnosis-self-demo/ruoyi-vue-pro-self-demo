package openplat;

import openplat.domain.OpenplatAppAuth;
import openplat.service.OpenplatAppAuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 开放平台应用认证服务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenplatAppAuthServiceTest {
    
    @Autowired
    private OpenplatAppAuthService appAuthService;
    
    @Test
    public void testSave() {
        OpenplatAppAuth appAuth = new OpenplatAppAuth();
        appAuth.setAppId("test_app");
        appAuth.setAppSecret("test_secret");
        appAuth.setSystemId("sys_001");
        appAuth.setStatus("ENABLED");
        appAuth.setDescription("测试应用");
        appAuth.setCreateUserId("test");
        appAuth.setUpdateUserId("test");
        
        int result = appAuthService.save(appAuth);
        assert result > 0;
    }
    
    @Test
    public void testGetByAppId() {
        OpenplatAppAuth appAuth = appAuthService.getByAppId("app_srm_001");
        assert appAuth != null;
        assert "app_srm_001".equals(appAuth.getAppId());
    }
    
    @Test
    public void testValidateAppAuth() {
        boolean result = appAuthService.validateAppAuth("app_srm_001", "secret_srm_2024");
        assert result;
    }
    
    @Test
    public void testList() {
        OpenplatAppAuth query = new OpenplatAppAuth();
        query.setSystemId("sys_001");
        List<OpenplatAppAuth> list = appAuthService.list(query);
        assert list != null;
    }
}
