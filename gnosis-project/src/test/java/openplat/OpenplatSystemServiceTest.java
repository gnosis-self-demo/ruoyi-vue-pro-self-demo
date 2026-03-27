package openplat;

import openplat.domain.OpenplatSystem;
import openplat.service.OpenplatSystemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 开放平台系统服务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenplatSystemServiceTest {
    
    @Autowired
    private OpenplatSystemService systemService;
    
    @Test
    public void testSave() {
        OpenplatSystem system = new OpenplatSystem();
        system.setSystemCode("TEST_SYS");
        system.setSystemName("测试系统");
        system.setSystemType("INTERNAL");
        system.setPrincipal("测试人员");
        system.setStatus("ENABLED");
        system.setDescription("测试描述");
        system.setCreateUserId("test");
        system.setUpdateUserId("test");
        
        int result = systemService.save(system);
        assert result > 0;
    }
    
    @Test
    public void testGetById() {
        OpenplatSystem system = systemService.getById("sys_001");
        assert system != null;
        assert "SRM".equals(system.getSystemCode());
    }
    
    @Test
    public void testList() {
        OpenplatSystem query = new OpenplatSystem();
        query.setSystemCode("SRM");
        List<OpenplatSystem> list = systemService.list(query);
        assert list != null;
        assert list.size() > 0;
    }
    
    @Test
    public void testUpdate() {
        OpenplatSystem system = systemService.getById("sys_001");
        system.setSystemName("更新后的系统名称");
        int result = systemService.update(system);
        assert result > 0;
    }
    
    @Test
    public void testBatchEnable() {
        String[] ids = {"sys_001"};
        int result = systemService.batchEnable(ids);
        assert result > 0;
    }
    
    @Test
    public void testBatchDisable() {
        String[] ids = {"sys_001"};
        int result = systemService.batchDisable(ids);
        assert result > 0;
    }
}
