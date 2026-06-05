package com.gnosis.datamapping.controller;

import com.gnosis.common.dto.BaseResponse;
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
    public BaseResponse<PageResult<DataMappingTestCaseVO>> pageList(@RequestBody PageRequest<DataMappingTestCaseQueryRequest> request) {
        return BaseResponse.success(testCaseService.pageList(request));
    }

    @ApiOperation("查询测试用例详情")
    @PostMapping("/detail")
    public BaseResponse<DataMappingTestCaseVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return BaseResponse.success(testCaseService.detail(id));
    }

    @ApiOperation("新建测试用例")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody DataMappingTestCaseCreateRequest request) {
        testCaseService.create(request);
        return BaseResponse.success();
    }

    @ApiOperation("编辑测试用例")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody DataMappingTestCaseUpdateRequest request) {
        testCaseService.update(request);
        return BaseResponse.success();
    }

    @ApiOperation("删除测试用例")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        testCaseService.delete(id);
        return BaseResponse.success();
    }

    @ApiOperation("批量删除测试用例")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody DataMappingTestCaseIdsRequest request) {
        testCaseService.batchDelete(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量启用测试用例")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody DataMappingTestCaseIdsRequest request) {
        testCaseService.batchEnable(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量禁用测试用例")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody DataMappingTestCaseIdsRequest request) {
        testCaseService.batchDisable(request);
        return BaseResponse.success();
    }

    @ApiOperation("执行单个测试用例并对比结果")
    @PostMapping("/execute")
    public BaseResponse<DataMappingTestCaseExecuteResponse> execute(@RequestBody DataMappingTestCaseExecuteRequest request) {
        return BaseResponse.success(testCaseService.execute(request.getTestCaseId()));
    }

    @ApiOperation("批量执行配置下所有测试用例")
    @PostMapping("/executeByConfig")
    public BaseResponse<DataMappingBatchExecuteResponse> executeByConfig(@RequestBody Map<String, String> request) {
        String configId = request.get("configId");
        return BaseResponse.success(testCaseService.executeByConfigId(configId));
    }
}
