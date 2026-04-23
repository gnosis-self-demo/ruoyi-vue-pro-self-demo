package com.gnosis.datamapping.service;

import com.alibaba.fastjson.JSON;
import com.gnosis.datamapping.domain.DataMappingConfig;
import com.gnosis.datamapping.dto.DataMappingExecuteResponse;
import com.gnosis.datamapping.engine.ErrorDetail;
import com.gnosis.datamapping.engine.MappingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataMappingEngineService {

    @Autowired
    private DataMappingConfigService configService;

    @Autowired
    private DataMappingExecutionLogService logService;

    private MappingEngine mappingEngine = new MappingEngine();

    public DataMappingExecuteResponse execute(String configId, String requestJson) {
        long startTime = System.currentTimeMillis();

        DataMappingConfig config = configService.detail(configId) != null ?
                convertVOToConfig(configService.detail(configId)) : null;

        if (config == null || config.getConfigJson() == null) {
            DataMappingExecuteResponse response = new DataMappingExecuteResponse();
            response.setSuccess(false);
            List<ErrorDetail> errors = new ArrayList<>();
            errors.add(new ErrorDetail("config", "notFound", "配置不存在或配置JSON为空", configId));
            response.setErrors(errors);
            return response;
        }

        try {
            Map<String, Object> result = mappingEngine.execute(config.getConfigJson(), requestJson);

            DataMappingExecuteResponse response = new DataMappingExecuteResponse();
            response.setSuccess((Boolean) result.get("success"));
            response.setResultData(result.get("resultData"));
            response.setErrors((List<ErrorDetail>) result.get("errors"));
            response.setValidationErrors((List<ErrorDetail>) result.get("validationErrors"));

            int executionTime = (int) (System.currentTimeMillis() - startTime);
            response.setExecutionTime(executionTime);

            String resultJson = JSON.toJSONString(result);
            String errorInfo = null;
            if (result.get("errors") != null && !((List<?>) result.get("errors")).isEmpty()) {
                errorInfo = JSON.toJSONString(result.get("errors"));
            }

            logService.saveLog(configId, config.getConfigCode(), requestJson,
                    resultJson, config.getConfigJson(), (Boolean) result.get("success"),
                    errorInfo, executionTime);

            return response;
        } catch (Exception e) {
            DataMappingExecuteResponse response = new DataMappingExecuteResponse();
            response.setSuccess(false);
            List<ErrorDetail> errors = new ArrayList<>();
            errors.add(new ErrorDetail("engine", "execute", "执行异常: " + e.getMessage(), null));
            response.setErrors(errors);
            response.setExecutionTime((int) (System.currentTimeMillis() - startTime));

            logService.saveLog(configId, config.getConfigCode(), requestJson,
                    null, config.getConfigJson(), false, e.getMessage(),
                    (int) (System.currentTimeMillis() - startTime));

            return response;
        }
    }

    private DataMappingConfig convertVOToConfig(com.gnosis.datamapping.dto.DataMappingConfigVO vo) {
        DataMappingConfig config = new DataMappingConfig();
        config.setId(vo.getId());
        config.setConfigName(vo.getConfigName());
        config.setConfigCode(vo.getConfigCode());
        config.setSystemId(vo.getSystemId());
        config.setSystemName(vo.getSystemName());
        config.setConfigVersion(vo.getConfigVersion());
        config.setApiVersion(vo.getApiVersion());
        config.setTargetEndpoint(vo.getTargetEndpoint());
        config.setJsonRootPath(vo.getJsonRootPath());
        config.setConfigJson(vo.getConfigJson());
        config.setDescription(vo.getDescription());
        config.setStatus(vo.getStatus());
        config.setCreateUserId(vo.getCreateUserId());
        config.setUpdateUserId(vo.getUpdateUserId());
        config.setCreateTime(vo.getCreateTime());
        config.setUpdateTime(vo.getUpdateTime());
        return config;
    }
}
