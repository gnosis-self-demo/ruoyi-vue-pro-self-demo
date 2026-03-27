package openplat.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import openplat.domain.OpenplatAppAuth;
import openplat.mapper.OpenplatAppAuthMapper;
import openplat.service.OpenplatAppAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 应用认证服务实现
 */
@Slf4j
@Service
public class OpenplatAppAuthServiceImpl implements OpenplatAppAuthService {
    
    @Autowired
    private OpenplatAppAuthMapper appAuthMapper;
    
    @Override
    public OpenplatAppAuth getById(String id) {
        return appAuthMapper.selectById(id);
    }
    
    @Override
    public OpenplatAppAuth getByAppId(String appId) {
        return appAuthMapper.selectByAppId(appId);
    }
    
    @Override
    public List<OpenplatAppAuth> list(OpenplatAppAuth appAuth) {
        return appAuthMapper.selectList(appAuth);
    }
    
    @Override
    public int save(OpenplatAppAuth appAuth) {
        appAuth.setId(IdUtil.getSnowflakeNextIdStr());
        appAuth.setCreateTime(new Date());
        appAuth.setUpdateTime(new Date());
        return appAuthMapper.insert(appAuth);
    }
    
    @Override
    public int update(OpenplatAppAuth appAuth) {
        appAuth.setUpdateTime(new Date());
        return appAuthMapper.update(appAuth);
    }
    
    @Override
    public int delete(String id) {
        return appAuthMapper.deleteById(id);
    }
    
    @Override
    public int batchDelete(String[] ids) {
        return appAuthMapper.deleteByIds(ids);
    }
    
    @Override
    public int batchEnable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatAppAuth appAuth = new OpenplatAppAuth();
            appAuth.setId(id);
            appAuth.setStatus("ENABLED");
            appAuth.setUpdateTime(new Date());
            count += appAuthMapper.update(appAuth);
        }
        return count;
    }
    
    @Override
    public int batchDisable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatAppAuth appAuth = new OpenplatAppAuth();
            appAuth.setId(id);
            appAuth.setStatus("DISABLED");
            appAuth.setUpdateTime(new Date());
            count += appAuthMapper.update(appAuth);
        }
        return count;
    }
    
    @Override
    public boolean validateAppAuth(String appId, String appSecret) {
        OpenplatAppAuth appAuth = appAuthMapper.selectByAppId(appId);
        if (appAuth == null || !"ENABLED".equals(appAuth.getStatus())) {
            return false;
        }
        return appSecret.equals(appAuth.getAppSecret());
    }
}
