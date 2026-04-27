package com.gnosis.datamapping.controller;

import com.gnosis.common.dto.CommonResponse;
import com.gnosis.datamapping.dto.*;
import com.gnosis.datamapping.service.DataMappingTestCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "数据映射测试用例管理")
@RestController
@RequestMapping("/datamapping/testcase")
public class DataMappingTestCaseController {

    @Autowired
    private DataMappingTestCaseService testCaseService;

    @ApiOperation("分页查询测试用例列表")
    @PostMapping("/page")
    public CommonResponse<PageResult<DataMappingTestCaseVO>> pageList(@RequestBody PageRequest<DataMappingTestCaseQueryRequest> request) {
        return CommonResponse.success(testCaseService.pageList(request));
    }

    @ApiOperation("查询测试用例详情")
    @PostMapping("/detail")
    public CommonResponse<DataMappingTestCaseVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return CommonResponse.success(testCaseService.detail(id));
    }

    @ApiOperation("新建测试用例")
    @PostMapping("/create")
    public CommonResponse<Void> create(@RequestBody DataMappingTestCaseCreateRequest request) {
        testCaseService.create(request);
        return CommonResponse.success();
    }

    @ApiOperation("编辑测试用例")
    @PostMapping("/update")
    public CommonResponse<Void> update(@RequestBody DataMappingTestCaseUpdateRequest request) {
        testCaseService.update(request);
        return CommonResponse.success();
    }

    @ApiOperation("删除测试用例")
    @PostMapping("/delete")
    public CommonResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        testCaseService.delete(id);
        return CommonResponse.success();
    }

    @ApiOperation("批量删除测试用例")
    @PostMapping("/batchDelete")
    public CommonResponse<Void> batchDelete(@RequestBody DataMappingTestCaseIdsRequest request) {
        testCaseService.batchDelete(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量启用测试用例")
    @PostMapping("/batchEnable")
    public CommonResponse<Void> batchEnable(@RequestBody DataMappingTestCaseIdsRequest request) {
        testCaseService.batchEnable(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量禁用测试用例")
    @PostMapping("/batchDisable")
    public CommonResponse<Void> batchDisable(@RequestBody DataMappingTestCaseIdsRequest request) {
        testCaseService.batchDisable(request);
        return CommonResponse.success();
    }

    @ApiOperation("执行单个测试用例并对比结果")
    @PostMapping("/execute")
    public CommonResponse<DataMappingTestCaseExecuteResponse> execute(@RequestBody DataMappingTestCaseExecuteRequest request) {
        return CommonResponse.success(testCaseService.execute(request.getTestCaseId()));
    }

    @ApiOperation("批量执行配置下所有测试用例")
    @PostMapping("/executeByConfig")
    public CommonResponse<DataMappingBatchExecuteResponse> executeByConfig(@RequestBody Map<String, String> request) {
        String configId = request.get("configId");
        return CommonResponse.success(testCaseService.executeByConfigId(configId));
    }
}
