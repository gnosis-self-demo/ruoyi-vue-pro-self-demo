package com.gnosis.datamapping.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.datamapping.dto.*;
import com.gnosis.datamapping.service.DataMappingConfigService;
import com.gnosis.datamapping.service.DataMappingEngineService;
import com.gnosis.datamapping.service.DataMappingImportExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = "数据映射配置管理")
@RestController
@RequestMapping("/datamapping/config")
public class DataMappingConfigController {

    @Autowired
    private DataMappingConfigService configService;

    @Autowired
    private DataMappingEngineService engineService;

    @Autowired
    private DataMappingImportExportService importExportService;

    @ApiOperation("分页查询配置列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<DataMappingConfigVO>> pageList(@RequestBody PageRequest<DataMappingConfigQueryRequest> request) {
        return BaseResponse.success(configService.pageList(request));
    }

    @ApiOperation("查询配置详情")
    @PostMapping("/detail")
    public BaseResponse<DataMappingConfigVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return BaseResponse.success(configService.detail(id));
    }

    @ApiOperation("新建配置")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody DataMappingConfigCreateRequest request) {
        configService.create(request);
        return BaseResponse.success();
    }

    @ApiOperation("编辑配置")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody DataMappingConfigUpdateRequest request) {
        configService.update(request);
        return BaseResponse.success();
    }

    @ApiOperation("删除配置")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        configService.delete(id);
        return BaseResponse.success();
    }

    @ApiOperation("批量删除配置")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody DataMappingConfigIdsRequest request) {
        configService.batchDelete(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量启用配置")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody DataMappingConfigIdsRequest request) {
        configService.batchEnable(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量禁用配置")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody DataMappingConfigIdsRequest request) {
        configService.batchDisable(request);
        return BaseResponse.success();
    }

    @ApiOperation("执行映射")
    @PostMapping("/execute")
    public BaseResponse<DataMappingExecuteResponse> execute(@RequestBody DataMappingExecuteRequest request) {
        return BaseResponse.success(engineService.execute(request.getConfigId(), request.getRequestJson()));
    }

    @ApiOperation("模拟器测试")
    @PostMapping("/test")
    public BaseResponse<DataMappingExecuteResponse> test(@RequestBody DataMappingTestRequest request) {
        return BaseResponse.success(engineService.execute(request.getConfigId(), request.getRequestJson()));
    }

    @ApiOperation("导出配置")
    @PostMapping("/export")
    public void export(@RequestBody DataMappingConfigIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportConfigs(request, response);
    }

    @ApiOperation("导入配置")
    @PostMapping("/import")
    public BaseResponse<Void> importConfigs(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importConfigs(file);
            return BaseResponse.success();
        } catch (Exception e) {
            return BaseResponse.error("导入失败: " + e.getMessage());
        }
    }
}
