package com.gnosis.process.controller;

import com.gnosis.process.domain.ProcessOrchestrationConfig;
import com.gnosis.process.dto.CommonResponse;
import com.gnosis.process.dto.IdRequest;
import com.gnosis.process.dto.IdsRequest;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.dto.ProcessOrchestrationConfigCreateRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigQueryRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigUpdateRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigVO;
import com.gnosis.process.service.ProcessOrchestrationConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 流程编排配置管理 Controller
 */
@RestController
@RequestMapping("/api/process-orchestration/orchestration-config")
@Api(tags = "流程编排配置管理")
public class ProcessOrchestrationConfigController {

    @Autowired
    private ProcessOrchestrationConfigService processOrchestrationConfigService;

    @PostMapping("/list")
    @ApiOperation(value = "分页查询流程编排配置列表")
    public CommonResponse<PageResult<ProcessOrchestrationConfigVO>> list(@RequestBody ProcessOrchestrationConfigQueryRequest request) {
        PageResult<ProcessOrchestrationConfigVO> result = processOrchestrationConfigService.list(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "查询流程编排配置详情")
    public CommonResponse<ProcessOrchestrationConfig> detail(@RequestBody IdRequest request) {
        ProcessOrchestrationConfig config = processOrchestrationConfigService.getById(request.getId());
        return CommonResponse.success(config);
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增流程编排配置")
    public CommonResponse<String> save(@RequestBody ProcessOrchestrationConfigCreateRequest request) {
        int rows = processOrchestrationConfigService.save(request);
        return CommonResponse.success("新增成功，影响行数：" + rows);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新流程编排配置")
    public CommonResponse<String> update(@RequestBody ProcessOrchestrationConfigUpdateRequest request) {
        int rows = processOrchestrationConfigService.update(request);
        return CommonResponse.success("更新成功，影响行数：" + rows);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除流程编排配置")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        int rows = processOrchestrationConfigService.delete(request.getId());
        return CommonResponse.success("删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchDelete")
    @ApiOperation(value = "批量删除流程编排配置")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        int rows = processOrchestrationConfigService.batchDelete(request.getIds());
        return CommonResponse.success("批量删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchEnable")
    @ApiOperation(value = "批量启用流程编排配置")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        int rows = processOrchestrationConfigService.batchEnable(request.getIds(), "system");
        return CommonResponse.success("批量启用成功，影响行数：" + rows);
    }

    @PostMapping("/batchDisable")
    @ApiOperation(value = "批量禁用流程编排配置")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        int rows = processOrchestrationConfigService.batchDisable(request.getIds(), "system");
        return CommonResponse.success("批量禁用成功，影响行数：" + rows);
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入流程编排配置数据")
    public CommonResponse<String> importData(@RequestParam("file") MultipartFile file) {
        processOrchestrationConfigService.importData(file, "system");
        return CommonResponse.success("导入成功");
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出流程编排配置数据")
    public void exportData(@RequestBody ProcessOrchestrationConfigQueryRequest request, HttpServletResponse response) {
        processOrchestrationConfigService.exportData(request, response);
    }
}
