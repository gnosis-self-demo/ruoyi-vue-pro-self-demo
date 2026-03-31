package com.gnosis.openplat.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import com.gnosis.openplat.domain.OpenplatAccessLog;
import com.gnosis.openplat.mapper.OpenplatAccessLogMapper;
import com.gnosis.openplat.service.OpenplatAccessLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 调用日志服务实现
 */
@Slf4j
@Service
public class OpenplatAccessLogServiceImpl implements OpenplatAccessLogService {
    
    @Autowired
    private OpenplatAccessLogMapper accessLogMapper;
    
    @Override
    public OpenplatAccessLog getById(String id) {
        return accessLogMapper.selectById(id);
    }
    
    @Override
    public List<OpenplatAccessLog> list(OpenplatAccessLog accessLog) {
        return accessLogMapper.selectList(accessLog);
    }
    
    @Override
    public int save(OpenplatAccessLog accessLog) {
        accessLog.setId(IdUtil.getSnowflakeNextIdStr());
        accessLog.setCreateTime(new Date());
        return accessLogMapper.insert(accessLog);
    }
    
    @Override
    public int delete(String id) {
        return accessLogMapper.deleteById(id);
    }
    
    @Override
    public int batchDelete(String[] ids) {
        return accessLogMapper.deleteByIds(ids);
    }
    
    @Override
    public int cleanExpiredLogs(Date expireDate) {
        return accessLogMapper.deleteExpired(expireDate);
    }
}
