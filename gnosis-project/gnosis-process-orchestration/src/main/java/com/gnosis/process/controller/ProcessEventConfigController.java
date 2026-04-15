package com.gnosis.process.controller;

import com.gnosis.process.domain.ProcessEventConfig;
import com.gnosis.process.dto.CommonResponse;
import com.gnosis.process.dto.IdRequest;
import com.gnosis.process.dto.IdsRequest;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.dto.ProcessEventConfigCreateRequest;
import com.gnosis.process.dto.ProcessEventConfigQueryRequest;
import com.gnosis.process.dto.ProcessEventConfigUpdateRequest;
import com.gnosis.process.dto.ProcessEventConfigVO;
import com.gnosis.process.service.ProcessEventConfigService;
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
 * 事件配置管理 Controller
 */
@RestController
@RequestMapping("/api/process-orchestration/event-config")
@Api(tags = "事件配置管理")
public class ProcessEventConfigController {

    @Autowired
    private ProcessEventConfigService processEventConfigService;

    @PostMapping("/list")
    @ApiOperation(value = "分页查询事件配置列表")
    public CommonResponse<PageResult<ProcessEventConfigVO>> list(@RequestBody ProcessEventConfigQueryRequest request) {
        PageResult<ProcessEventConfigVO> result = processEventConfigService.list(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "查询事件配置详情")
    public CommonResponse<ProcessEventConfig> detail(@RequestBody IdRequest request) {
        ProcessEventConfig eventConfig = processEventConfigService.getById(request.getId());
        return CommonResponse.success(eventConfig);
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增事件配置")
    public CommonResponse<String> save(@RequestBody ProcessEventConfigCreateRequest request) {
        int rows = processEventConfigService.save(request);
        return CommonResponse.success("新增成功，影响行数：" + rows);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新事件配置")
    public CommonResponse<String> update(@RequestBody ProcessEventConfigUpdateRequest request) {
        int rows = processEventConfigService.update(request);
        return CommonResponse.success("更新成功，影响行数：" + rows);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除事件配置")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        int rows = processEventConfigService.delete(request.getId());
        return CommonResponse.success("删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchDelete")
    @ApiOperation(value = "批量删除事件配置")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        int rows = processEventConfigService.batchDelete(request.getIds());
        return CommonResponse.success("批量删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchEnable")
    @ApiOperation(value = "批量启用事件配置")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        int rows = processEventConfigService.batchEnable(request.getIds(), "system");
        return CommonResponse.success("批量启用成功，影响行数：" + rows);
    }

    @PostMapping("/batchDisable")
    @ApiOperation(value = "批量禁用事件配置")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        int rows = processEventConfigService.batchDisable(request.getIds(), "system");
        return CommonResponse.success("批量禁用成功，影响行数：" + rows);
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入事件配置数据")
    public CommonResponse<String> importData(@RequestParam("file") MultipartFile file) {
        processEventConfigService.importData(file, "system");
        return CommonResponse.success("导入成功");
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出事件配置数据")
    public void exportData(@RequestBody ProcessEventConfigQueryRequest request, HttpServletResponse response) {
        processEventConfigService.exportData(request, response);
    }
}
