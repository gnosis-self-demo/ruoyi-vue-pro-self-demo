package com.gnosis.openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatSystem;
import com.gnosis.openplat.dto.CommonResponse;
import com.gnosis.openplat.service.OpenplatSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对接系统管理 Controller
 */
@Api(tags = "对接系统管理")
@RestController
@RequestMapping("/openplat/system")
public class OpenplatSystemController {
    
    @Autowired
    private OpenplatSystemService systemService;
    
    @ApiOperation("分页查询系统列表")
    @GetMapping("/list")
    public CommonResponse<List<OpenplatSystem>> list() {
        List<OpenplatSystem> list = systemService.list(new OpenplatSystem());
        return CommonResponse.success(list);
    }
    
    @ApiOperation("分页查询系统列表")
    @PostMapping("/list")
    public CommonResponse<List<OpenplatSystem>> list(@RequestBody(required = false) OpenplatSystem system) {
        List<OpenplatSystem> list = systemService.list(system);
        return CommonResponse.success(list);
    }
    
    @ApiOperation("根据 ID 查询系统详情")
    @GetMapping("/{id}")
    public CommonResponse<OpenplatSystem> getById(@PathVariable String id) {
        OpenplatSystem system = systemService.getById(id);
        return CommonResponse.success(system);
    }
    
    @ApiOperation("新增系统")
    @PostMapping
    public CommonResponse<String> save(@RequestBody OpenplatSystem system) {
        systemService.save(system);
        return CommonResponse.success("新增成功");
    }
    
    @ApiOperation("修改系统")
    @PutMapping
    public CommonResponse<String> update(@RequestBody OpenplatSystem system) {
        systemService.update(system);
        return CommonResponse.success("修改成功");
    }
    
    @ApiOperation("删除系统")
    @DeleteMapping("/{id}")
    public CommonResponse<String> delete(@PathVariable String id) {
        systemService.delete(id);
        return CommonResponse.success("删除成功");
    }
    
    @ApiOperation("批量删除系统")
    @DeleteMapping("/batch")
    public CommonResponse<String> batchDelete(@RequestBody String[] ids) {
        systemService.batchDelete(ids);
        return CommonResponse.success("批量删除成功");
    }
    
    @ApiOperation("批量启用系统")
    @PutMapping("/enable")
    public CommonResponse<String> batchEnable(@RequestBody String[] ids) {
        systemService.batchEnable(ids);
        return CommonResponse.success("批量启用成功");
    }
    
    @ApiOperation("批量禁用系统")
    @PutMapping("/disable")
    public CommonResponse<String> batchDisable(@RequestBody String[] ids) {
        systemService.batchDisable(ids);
        return CommonResponse.success("批量禁用成功");
    }
    
    @ApiOperation("查询系统关联的API列表")
    @GetMapping("/apis/{systemId}")
    public CommonResponse<List<OpenplatApiConfig>> getSystemApis(@PathVariable String systemId) {
        List<OpenplatApiConfig> apis = systemService.getSystemApis(systemId);
        return CommonResponse.success(apis);
    }
}
