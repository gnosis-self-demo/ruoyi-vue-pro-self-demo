package com.gnosis.openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.dto.CommonResponse;
import com.gnosis.openplat.service.OpenplatApiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API 配置管理 Controller
 */
@Api(tags = "API 配置管理")
@RestController
@RequestMapping("/openplat/api-config")
public class OpenplatApiConfigController {
    
    @Autowired
    private OpenplatApiConfigService apiConfigService;
    
    @ApiOperation("分页查询 API 配置列表")
    @GetMapping("/list")
    @PostMapping("/list")
    public CommonResponse<List<OpenplatApiConfig>> list(@RequestBody(required = false) OpenplatApiConfig apiConfig) {
        List<OpenplatApiConfig> list = apiConfigService.list(apiConfig);
        return CommonResponse.success(list);
    }
    
    @ApiOperation("根据 ID 查询 API 配置详情")
    @GetMapping("/{id}")
    public CommonResponse<OpenplatApiConfig> getById(@PathVariable String id) {
        OpenplatApiConfig apiConfig = apiConfigService.getById(id);
        return CommonResponse.success(apiConfig);
    }
    
    @ApiOperation("新增 API 配置")
    @PostMapping
    public CommonResponse<String> save(@RequestBody OpenplatApiConfig apiConfig) {
        apiConfigService.save(apiConfig);
        return CommonResponse.success("新增成功");
    }
    
    @ApiOperation("修改 API 配置")
    @PutMapping
    public CommonResponse<String> update(@RequestBody OpenplatApiConfig apiConfig) {
        apiConfigService.update(apiConfig);
        return CommonResponse.success("修改成功");
    }
    
    @ApiOperation("删除 API 配置")
    @DeleteMapping("/{id}")
    public CommonResponse<String> delete(@PathVariable String id) {
        apiConfigService.delete(id);
        return CommonResponse.success("删除成功");
    }
    
    @ApiOperation("批量删除 API 配置")
    @DeleteMapping("/batch")
    public CommonResponse<String> batchDelete(@RequestBody String[] ids) {
        apiConfigService.batchDelete(ids);
        return CommonResponse.success("批量删除成功");
    }
    
    @ApiOperation("批量启用 API")
    @PutMapping("/enable")
    public CommonResponse<String> batchEnable(@RequestBody String[] ids) {
        apiConfigService.batchEnable(ids);
        return CommonResponse.success("批量启用成功");
    }
    
    @ApiOperation("批量禁用 API")
    @PutMapping("/disable")
    public CommonResponse<String> batchDisable(@RequestBody String[] ids) {
        apiConfigService.batchDisable(ids);
        return CommonResponse.success("批量禁用成功");
    }
    
    @ApiOperation("检查 API 配置是否存在")
    @GetMapping("/check")
    public CommonResponse<Boolean> checkApiConfig(@RequestParam String apiCode, @RequestParam String apiPath) {
        OpenplatApiConfig byApiCode = apiConfigService.getByApiCode(apiCode);
        OpenplatApiConfig byApiPath = apiConfigService.getByApiPath(apiPath);
        boolean exists = byApiCode != null || byApiPath != null;
        return CommonResponse.success(exists);
    }
    
    @ApiOperation("检查 API 配置是否存在（POST方式）")
    @PostMapping("/check")
    public CommonResponse<Boolean> checkApiConfigPost(@RequestBody Map<String, Object> requestBody) {
        String apiCode = (String) requestBody.get("apiCode");
        String apiPath = (String) requestBody.get("apiPath");
        OpenplatApiConfig byApiCode = apiConfigService.getByApiCode(apiCode);
        OpenplatApiConfig byApiPath = apiConfigService.getByApiPath(apiPath);
        boolean exists = byApiCode != null || byApiPath != null;
        return CommonResponse.success(exists);
    }
}
