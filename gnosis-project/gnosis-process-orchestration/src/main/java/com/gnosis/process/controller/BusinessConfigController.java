package com.gnosis.process.controller;

import com.gnosis.process.domain.BusinessConfig;
import com.gnosis.process.dto.BusinessConfigCreateRequest;
import com.gnosis.process.dto.BusinessConfigQueryRequest;
import com.gnosis.process.dto.BusinessConfigUpdateRequest;
import com.gnosis.process.dto.BusinessConfigVO;
import com.gnosis.process.dto.CommonResponse;
import com.gnosis.process.dto.IdRequest;
import com.gnosis.process.dto.IdsRequest;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.service.BusinessConfigService;
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
 * 业务配置管理 Controller
 */
@RestController
@RequestMapping("/api/process-orchestration/business-config")
@Api(tags = "业务配置管理")
public class BusinessConfigController {

    @Autowired
    private BusinessConfigService businessConfigService;

    @PostMapping("/list")
    @ApiOperation(value = "分页查询业务配置列表")
    public CommonResponse<PageResult<BusinessConfigVO>> list(@RequestBody BusinessConfigQueryRequest request) {
        PageResult<BusinessConfigVO> result = businessConfigService.list(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "查询业务配置详情")
    public CommonResponse<BusinessConfig> detail(@RequestBody IdRequest request) {
        BusinessConfig businessConfig = businessConfigService.getById(request.getId());
        return CommonResponse.success(businessConfig);
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增业务配置")
    public CommonResponse<String> save(@RequestBody BusinessConfigCreateRequest request) {
        int rows = businessConfigService.save(request);
        return CommonResponse.success("新增成功，影响行数：" + rows);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新业务配置")
    public CommonResponse<String> update(@RequestBody BusinessConfigUpdateRequest request) {
        int rows = businessConfigService.update(request);
        return CommonResponse.success("更新成功，影响行数：" + rows);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除业务配置")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        int rows = businessConfigService.delete(request.getId());
        return CommonResponse.success("删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchDelete")
    @ApiOperation(value = "批量删除业务配置")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        int rows = businessConfigService.batchDelete(request.getIds());
        return CommonResponse.success("批量删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchEnable")
    @ApiOperation(value = "批量启用业务配置")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        int rows = businessConfigService.batchEnable(request.getIds(), "system");
        return CommonResponse.success("批量启用成功，影响行数：" + rows);
    }

    @PostMapping("/batchDisable")
    @ApiOperation(value = "批量禁用业务配置")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        int rows = businessConfigService.batchDisable(request.getIds(), "system");
        return CommonResponse.success("批量禁用成功，影响行数：" + rows);
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入业务配置数据")
    public CommonResponse<String> importData(@RequestParam("file") MultipartFile file) {
        businessConfigService.importData(file, "system");
        return CommonResponse.success("导入成功");
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出业务配置数据")
    public void exportData(@RequestBody BusinessConfigQueryRequest request, HttpServletResponse response) {
        businessConfigService.exportData(request, response);
    }
}
