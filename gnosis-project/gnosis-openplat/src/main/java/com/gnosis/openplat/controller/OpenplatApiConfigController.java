package com.gnosis.openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.dto.*;
import com.gnosis.openplat.service.OpenplatApiConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "API 配置管理")
@RestController
@RequestMapping("/openplat/api-config")
public class OpenplatApiConfigController {

    @Autowired
    private OpenplatApiConfigService apiConfigService;

    @ApiOperation("查询 API 配置列表")
    @PostMapping("/list")
    public CommonResponse<List<OpenplatApiConfig>> list(@RequestBody(required = false) OpenplatApiConfig apiConfig) {
        List<OpenplatApiConfig> list = apiConfigService.list(apiConfig);
        return CommonResponse.success(list);
    }

    @ApiOperation("根据 ID 查询 API 配置详情")
    @PostMapping("/detail")
    public CommonResponse<OpenplatApiConfig> detail(@RequestBody IdRequest request) {
        OpenplatApiConfig apiConfig = apiConfigService.getById(request.getId());
        return CommonResponse.success(apiConfig);
    }

    @ApiOperation("新增 API 配置")
    @PostMapping("/save")
    public CommonResponse<String> save(@RequestBody OpenplatApiConfig apiConfig) {
        apiConfigService.save(apiConfig);
        return CommonResponse.success("新增成功");
    }

    @ApiOperation("修改 API 配置")
    @PostMapping("/update")
    public CommonResponse<String> update(@RequestBody OpenplatApiConfig apiConfig) {
        apiConfigService.update(apiConfig);
        return CommonResponse.success("修改成功");
    }

    @ApiOperation("删除 API 配置")
    @PostMapping("/delete")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        apiConfigService.delete(request.getId());
        return CommonResponse.success("删除成功");
    }

    @ApiOperation("批量删除 API 配置")
    @PostMapping("/batchDelete")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        apiConfigService.batchDelete(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量删除成功");
    }

    @ApiOperation("批量启用 API")
    @PostMapping("/batchEnable")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        apiConfigService.batchEnable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量启用成功");
    }

    @ApiOperation("批量禁用 API")
    @PostMapping("/batchDisable")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        apiConfigService.batchDisable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量禁用成功");
    }

    @ApiOperation("检查 API 配置是否存在")
    @PostMapping("/check")
    public CommonResponse<Boolean> check(@RequestBody ApiConfigCheckRequest request) {
        OpenplatApiConfig byApiCode = apiConfigService.getByApiCode(request.getApiCode());
        OpenplatApiConfig byApiPath = apiConfigService.getByApiPath(request.getApiPath());
        boolean exists = byApiCode != null || byApiPath != null;
        return CommonResponse.success(exists);
    }

    @ApiOperation("批量关联系统到API")
    @PostMapping("/relateSystems")
    public CommonResponse<String> relateSystems(@RequestBody RelateSystemsRequest request) {
        log.info("relateSystems called, apiId={}, systemIds={}", request.getApiId(), request.getSystemIds());
        boolean result = apiConfigService.relateSystems(request.getApiId(), request.getSystemIds().toArray(new String[0]));
        log.info("relateSystems result: {}", result);
        return CommonResponse.success(result ? "关联成功" : "关联失败");
    }

    @ApiOperation("批量解除API与系统的关联")
    @PostMapping("/unrelateSystems")
    public CommonResponse<String> unrelateSystems(@RequestBody RelateSystemsRequest request) {
        apiConfigService.unrelateSystems(request.getApiId(), request.getSystemIds().toArray(new String[0]));
        return CommonResponse.success("解除关联成功");
    }

    @ApiOperation("根据 API 编码查询详情（包含关联系统）")
    @GetMapping("/getByApiCode/{apiCode}")
    public CommonResponse<OpenplatApiConfig> getByApiCode(@PathVariable String apiCode) {
        OpenplatApiConfig apiConfig = apiConfigService.getByApiCode(apiCode);
        if (apiConfig != null) {
            // 确保关联系统信息已加载
            return CommonResponse.success(apiConfigService.getById(apiConfig.getId()));
        }
        return CommonResponse.success(null);
    }
}
