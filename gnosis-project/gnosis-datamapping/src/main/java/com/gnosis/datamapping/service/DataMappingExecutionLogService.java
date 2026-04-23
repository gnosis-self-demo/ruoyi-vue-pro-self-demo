package com.gnosis.datamapping.service;

import com.gnosis.datamapping.domain.DataMappingExecutionLog;
import com.gnosis.datamapping.mapper.DataMappingExecutionLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class DataMappingExecutionLogService {

    @Autowired
    private DataMappingExecutionLogMapper logMapper;

    public void saveLog(String configId, String configCode, String requestJson,
                        String resultJson, String configJson, boolean success,
                        String errorInfo, int executionTime) {
        DataMappingExecutionLog log = new DataMappingExecutionLog();
        log.setId(UUID.randomUUID().toString().replace("-", ""));
        log.setConfigId(configId);
        log.setConfigCode(configCode);
        log.setRequestJson(requestJson);
        log.setResultJson(resultJson);
        log.setConfigJson(configJson);
        log.setSuccess(success);
        log.setErrorInfo(errorInfo);
        log.setExecutionTime(executionTime);
        log.setCreateTime(new Date());
        log.setUpdateTime(new Date());

        logMapper.insert(log);
    }

    public List<DataMappingExecutionLog> getByConfigId(String configId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return logMapper.selectByConfigId(configId, offset, pageSize);
    }

    public Long countByConfigId(String configId) {
        return logMapper.countByConfigId(configId);
    }
}
