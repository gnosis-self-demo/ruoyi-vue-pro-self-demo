package com.gnosis.openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.gnosis.openplat.domain.OpenplatApiSystemRelation;
import com.gnosis.openplat.dto.CommonResponse;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API与系统关系配置管理 Controller
 */
@Api(tags = "API与系统关系配置管理")
@RestController
@RequestMapping("/openplat/api-system-relation")
public class OpenplatApiSystemRelationController {
    
    @Autowired
    private OpenplatApiSystemRelationService relationService;
    
    @ApiOperation("分页查询 API与系统关系配置列表")
    @GetMapping("/list")
    @PostMapping("/list")
    public CommonResponse<List<OpenplatApiSystemRelation>> list(@RequestBody(required = false) OpenplatApiSystemRelation relation) {
        List<OpenplatApiSystemRelation> list = relationService.list(relation);
        return CommonResponse.success(list);
    }
    
    @ApiOperation("根据 ID 查询 API与系统关系配置详情")
    @GetMapping("/{id}")
    public CommonResponse<OpenplatApiSystemRelation> getById(@PathVariable String id) {
        OpenplatApiSystemRelation relation = relationService.getById(id);
        return CommonResponse.success(relation);
    }
    
    @ApiOperation("新增 API与系统关系配置")
    @PostMapping
    public CommonResponse<String> save(@RequestBody OpenplatApiSystemRelation relation) {
        relationService.save(relation);
        return CommonResponse.success("新增成功");
    }
    
    @ApiOperation("修改 API与系统关系配置")
    @PutMapping
    public CommonResponse<String> update(@RequestBody OpenplatApiSystemRelation relation) {
        relationService.update(relation);
        return CommonResponse.success("修改成功");
    }
    
    @ApiOperation("删除 API与系统关系配置")
    @DeleteMapping("/{id}")
    public CommonResponse<String> delete(@PathVariable String id) {
        relationService.delete(id);
        return CommonResponse.success("删除成功");
    }
    
    @ApiOperation("批量删除 API与系统关系配置")
    @DeleteMapping("/batch")
    public CommonResponse<String> batchDelete(@RequestBody String[] ids) {
        relationService.batchDelete(ids);
        return CommonResponse.success("批量删除成功");
    }
    
    @ApiOperation("批量启用 API与系统关系配置")
    @PutMapping("/enable")
    public CommonResponse<String> batchEnable(@RequestBody String[] ids) {
        relationService.batchEnable(ids);
        return CommonResponse.success("批量启用成功");
    }
    
    @ApiOperation("批量禁用 API与系统关系配置")
    @PutMapping("/disable")
    public CommonResponse<String> batchDisable(@RequestBody String[] ids) {
        relationService.batchDisable(ids);
        return CommonResponse.success("批量禁用成功");
    }
}