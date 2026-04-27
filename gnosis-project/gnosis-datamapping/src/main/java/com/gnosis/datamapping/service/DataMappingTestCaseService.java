package com.gnosis.datamapping.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.domain.DataMappingConfig;
import com.gnosis.datamapping.domain.DataMappingTestCase;
import com.gnosis.datamapping.dto.*;
import com.gnosis.datamapping.engine.MappingEngine;
import com.gnosis.datamapping.mapper.DataMappingConfigMapper;
import com.gnosis.datamapping.mapper.DataMappingTestCaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataMappingTestCaseService {

    @Autowired
    private DataMappingTestCaseMapper testCaseMapper;

    @Autowired
    private DataMappingConfigMapper configMapper;

    private MappingEngine mappingEngine = new MappingEngine();

    public PageResult<DataMappingTestCaseVO> pageList(PageRequest<DataMappingTestCaseQueryRequest> pageRequest) {
        DataMappingTestCaseQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new DataMappingTestCaseQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = testCaseMapper.countByCondition(query);
        if (total == 0) {
            PageResult<DataMappingTestCaseVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<DataMappingTestCase> testCases = testCaseMapper.selectByCondition(query);
        List<DataMappingTestCaseVO> voList = new ArrayList<>();
        for (DataMappingTestCase testCase : testCases) {
            voList.add(convertToVO(testCase));
        }

        PageResult<DataMappingTestCaseVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public DataMappingTestCaseVO detail(String id) {
        DataMappingTestCase testCase = testCaseMapper.selectById(id);
        return testCase != null ? convertToVO(testCase) : null;
    }

    public int create(DataMappingTestCaseCreateRequest request) {
        DataMappingTestCase testCase = new DataMappingTestCase();
        testCase.setId(UUID.randomUUID().toString().replace("-", ""));
        testCase.setConfigId(request.getConfigId());
        testCase.setConfigCode(request.getConfigCode());
        testCase.setCaseName(request.getCaseName());
        testCase.setCaseCode(request.getCaseCode());
        testCase.setRequestJson(request.getRequestJson());
        testCase.setExpectedResultJson(request.getExpectedResultJson());
        testCase.setActualResultJson(null);
        testCase.setIsPassed(0);
        testCase.setDiffInfo(null);
        testCase.setDescription(request.getDescription());
        testCase.setStatus(1);
        testCase.setCreateUserId(request.getCreateUserId());
        testCase.setUpdateUserId(request.getCreateUserId());
        testCase.setCreateTime(new Date());
        testCase.setUpdateTime(new Date());

        return testCaseMapper.insert(testCase);
    }

    public int update(DataMappingTestCaseUpdateRequest request) {
        DataMappingTestCase testCase = testCaseMapper.selectById(request.getId());
        if (testCase == null) {
            return 0;
        }

        testCase.setConfigId(request.getConfigId());
        testCase.setConfigCode(request.getConfigCode());
        testCase.setCaseName(request.getCaseName());
        testCase.setCaseCode(request.getCaseCode());
        testCase.setRequestJson(request.getRequestJson());
        testCase.setExpectedResultJson(request.getExpectedResultJson());
        testCase.setDescription(request.getDescription());
        testCase.setUpdateUserId(request.getUpdateUserId());
        testCase.setUpdateTime(new Date());

        return testCaseMapper.updateById(testCase);
    }

    public int delete(String id) {
        return testCaseMapper.deleteById(id);
    }

    public int batchDelete(DataMappingTestCaseIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return testCaseMapper.deleteByIds(request.getIds());
    }

    public int batchEnable(DataMappingTestCaseIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return testCaseMapper.batchUpdateStatus(request.getIds(), 1);
    }

    public int batchDisable(DataMappingTestCaseIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return testCaseMapper.batchUpdateStatus(request.getIds(), 0);
    }

    public DataMappingTestCaseExecuteResponse execute(String testCaseId) {
        DataMappingTestCase testCase = testCaseMapper.selectById(testCaseId);
        if (testCase == null) {
            DataMappingTestCaseExecuteResponse response = new DataMappingTestCaseExecuteResponse();
            response.setSuccess(false);
            response.setMessage("测试用例不存在");
            return response;
        }

        DataMappingConfig config = configMapper.selectById(testCase.getConfigId());
        if (config == null) {
            config = configMapper.selectByConfigCode(testCase.getConfigCode());
        }

        if (config == null || config.getConfigJson() == null) {
            DataMappingTestCaseExecuteResponse response = new DataMappingTestCaseExecuteResponse();
            response.setSuccess(false);
            response.setMessage("关联的配置不存在");
            return response;
        }

        long startTime = System.currentTimeMillis();
        Map<String, Object> result = mappingEngine.execute(config.getConfigJson(), testCase.getRequestJson());
        int executionTime = (int) (System.currentTimeMillis() - startTime);

        JSONObject actualResult = (JSONObject) result.get("resultData");
        String actualResultJson = actualResult != null ? actualResult.toJSONString() : "{}";

        JSONObject expectedResult = JSON.parseObject(testCase.getExpectedResultJson());

        boolean isPassed = compareJson(expectedResult, actualResult);
        String diffInfo = isPassed ? null : calculateDiff(expectedResult, actualResult);

        testCaseMapper.updateExecuteResult(testCaseId, actualResultJson, isPassed ? 1 : 0, diffInfo);

        DataMappingTestCaseExecuteResponse response = new DataMappingTestCaseExecuteResponse();
        response.setSuccess(true);
        response.setTestCaseId(testCaseId);
        response.setIsPassed(isPassed);
        response.setActualResult(actualResult);
        response.setDiffInfo(diffInfo);
        response.setExecutionTime(executionTime);

        return response;
    }

    public DataMappingBatchExecuteResponse executeByConfigId(String configId) {
        List<DataMappingTestCase> testCases = testCaseMapper.selectByConfigId(configId);
        if (testCases == null || testCases.isEmpty()) {
            DataMappingBatchExecuteResponse response = new DataMappingBatchExecuteResponse();
            response.setSuccess(false);
            response.setMessage("该配置下没有测试用例");
            response.setTotal(0);
            response.setPassed(0);
            response.setFailed(0);
            return response;
        }

        int total = testCases.size();
        int passed = 0;
        int failed = 0;
        List<DataMappingTestCaseExecuteResponse> results = new ArrayList<>();

        for (DataMappingTestCase testCase : testCases) {
            DataMappingTestCaseExecuteResponse result = execute(testCase.getId());
            if (result.getIsPassed()) {
                passed++;
            } else {
                failed++;
            }
            results.add(result);
        }

        DataMappingBatchExecuteResponse response = new DataMappingBatchExecuteResponse();
        response.setSuccess(true);
        response.setTotal(total);
        response.setPassed(passed);
        response.setFailed(failed);
        response.setResults(results);

        return response;
    }

    private boolean compareJson(JSONObject expected, JSONObject actual) {
        if (expected == null && actual == null) return true;
        if (expected == null || actual == null) return false;
        if (expected.size() != actual.size()) return false;

        for (String key : expected.keySet()) {
            if (!actual.containsKey(key)) return false;
            Object expectedValue = expected.get(key);
            Object actualValue = actual.get(key);

            if (expectedValue instanceof JSONObject && actualValue instanceof JSONObject) {
                if (!compareJson((JSONObject) expectedValue, (JSONObject) actualValue)) return false;
            } else if (expectedValue instanceof Number && actualValue instanceof Number) {
                double expectedNum = ((Number) expectedValue).doubleValue();
                double actualNum = ((Number) actualValue).doubleValue();
                if (Math.abs(expectedNum - actualNum) > 0.01) return false;
            } else {
                if (!String.valueOf(expectedValue).equals(String.valueOf(actualValue))) return false;
            }
        }
        return true;
    }

    private String calculateDiff(JSONObject expected, JSONObject actual) {
        StringBuilder diff = new StringBuilder();
        if (expected == null || actual == null) {
            return "期望或实际结果为空";
        }

        for (String key : expected.keySet()) {
            if (!actual.containsKey(key)) {
                diff.append("缺失字段: ").append(key).append("; ");
            } else {
                Object expectedValue = expected.get(key);
                Object actualValue = actual.get(key);
                if (!String.valueOf(expectedValue).equals(String.valueOf(actualValue))) {
                    diff.append("字段[").append(key).append("] 期望: ").append(expectedValue)
                        .append(", 实际: ").append(actualValue).append("; ");
                }
            }
        }

        for (String key : actual.keySet()) {
            if (!expected.containsKey(key)) {
                diff.append("多余字段: ").append(key).append("; ");
            }
        }

        return diff.length() > 0 ? diff.toString() : null;
    }

    private DataMappingTestCaseVO convertToVO(DataMappingTestCase testCase) {
        DataMappingTestCaseVO vo = new DataMappingTestCaseVO();
        vo.setId(testCase.getId());
        vo.setConfigId(testCase.getConfigId());
        vo.setConfigCode(testCase.getConfigCode());
        vo.setCaseName(testCase.getCaseName());
        vo.setCaseCode(testCase.getCaseCode());
        vo.setRequestJson(testCase.getRequestJson());
        vo.setExpectedResultJson(testCase.getExpectedResultJson());
        vo.setActualResultJson(testCase.getActualResultJson());
        vo.setIsPassed(testCase.getIsPassed());
        vo.setDiffInfo(testCase.getDiffInfo());
        vo.setDescription(testCase.getDescription());
        vo.setStatus(testCase.getStatus());
        vo.setCreateUserId(testCase.getCreateUserId());
        vo.setUpdateUserId(testCase.getUpdateUserId());
        vo.setCreateTime(testCase.getCreateTime());
        vo.setUpdateTime(testCase.getUpdateTime());
        return vo;
    }
}
