package com.gnosis.openplat.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatSystem;
import com.gnosis.openplat.dto.*;
import com.gnosis.openplat.service.OpenplatSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "对接系统管理")
@RestController
@RequestMapping("/openplat/system")
public class OpenplatSystemController {

    @Autowired
    private OpenplatSystemService systemService;

    @ApiOperation("查询系统列表")
    @PostMapping("/list")
    public CommonResponse<List<OpenplatSystem>> list(@RequestBody(required = false) OpenplatSystem system) {
        List<OpenplatSystem> list = systemService.list(system);
        return CommonResponse.success(list);
    }

    @ApiOperation("根据 ID 查询系统详情")
    @PostMapping("/detail")
    public CommonResponse<OpenplatSystem> detail(@RequestBody IdRequest request) {
        OpenplatSystem system = systemService.getById(request.getId());
        return CommonResponse.success(system);
    }

    @ApiOperation("新增系统")
    @PostMapping("/save")
    public CommonResponse<String> save(@RequestBody OpenplatSystem system) {
        systemService.save(system);
        return CommonResponse.success("新增成功");
    }

    @ApiOperation("修改系统")
    @PostMapping("/update")
    public CommonResponse<String> update(@RequestBody OpenplatSystem system) {
        systemService.update(system);
        return CommonResponse.success("修改成功");
    }

    @ApiOperation("删除系统")
    @PostMapping("/delete")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        systemService.delete(request.getId());
        return CommonResponse.success("删除成功");
    }

    @ApiOperation("批量删除系统")
    @PostMapping("/batchDelete")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        systemService.batchDelete(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量删除成功");
    }

    @ApiOperation("批量启用系统")
    @PostMapping("/batchEnable")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        systemService.batchEnable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量启用成功");
    }

    @ApiOperation("批量禁用系统")
    @PostMapping("/batchDisable")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        systemService.batchDisable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量禁用成功");
    }

    @ApiOperation("查询系统关联的API列表")
    @PostMapping("/getSystemApis")
    public CommonResponse<List<OpenplatApiConfig>> getSystemApis(@RequestBody IdRequest request) {
        List<OpenplatApiConfig> apis = systemService.getSystemApis(request.getId());
        return CommonResponse.success(apis);
    }

    @ApiOperation("根据系统编码查询系统详情（包含关联 API）")
    @GetMapping("/getBySystemCode/{systemCode}")
    public CommonResponse<JSONObject> getBySystemCode(@PathVariable String systemCode) {
        OpenplatSystem system = systemService.getBySystemCode(systemCode);
        if (system != null) {
            List<OpenplatApiConfig> apis = systemService.getSystemApis(system.getId());
            JSONObject result = new JSONObject();
            result.put("system", system);
            result.put("apis", apis);
            return CommonResponse.success(result);
        }
        return CommonResponse.success(null);
    }

    @ApiOperation("根据系统编码查询关联 API 列表")
    @GetMapping("/getApisBySystemCode/{systemCode}")
    public CommonResponse<List<OpenplatApiConfig>> getApisBySystemCode(@PathVariable String systemCode) {
        OpenplatSystem system = systemService.getBySystemCode(systemCode);
        if (system != null) {
            List<OpenplatApiConfig> apis = systemService.getSystemApis(system.getId());
            return CommonResponse.success(apis);
        }
        return CommonResponse.success(null);
    }
}
