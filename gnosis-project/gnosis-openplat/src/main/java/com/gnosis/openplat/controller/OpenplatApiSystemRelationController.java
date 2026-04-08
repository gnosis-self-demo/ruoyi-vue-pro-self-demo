package com.gnosis.openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.gnosis.openplat.domain.OpenplatApiSystemRelation;
import com.gnosis.openplat.dto.*;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "API与系统关系配置管理")
@RestController
@RequestMapping("/openplat/api-system-relation")
public class OpenplatApiSystemRelationController {

    @Autowired
    private OpenplatApiSystemRelationService relationService;

    @ApiOperation("查询 API与系统关系配置列表")
    @PostMapping("/list")
    public CommonResponse<List<OpenplatApiSystemRelation>> list(@RequestBody(required = false) OpenplatApiSystemRelation relation) {
        List<OpenplatApiSystemRelation> list = relationService.list(relation);
        return CommonResponse.success(list);
    }

    @ApiOperation("根据 ID 查询 API与系统关系配置详情")
    @PostMapping("/detail")
    public CommonResponse<OpenplatApiSystemRelation> detail(@RequestBody IdRequest request) {
        OpenplatApiSystemRelation relation = relationService.getById(request.getId());
        return CommonResponse.success(relation);
    }

    @ApiOperation("新增 API与系统关系配置")
    @PostMapping("/save")
    public CommonResponse<String> save(@RequestBody OpenplatApiSystemRelation relation) {
        relationService.save(relation);
        return CommonResponse.success("新增成功");
    }

    @ApiOperation("修改 API与系统关系配置")
    @PostMapping("/update")
    public CommonResponse<String> update(@RequestBody OpenplatApiSystemRelation relation) {
        relationService.update(relation);
        return CommonResponse.success("修改成功");
    }

    @ApiOperation("删除 API与系统关系配置")
    @PostMapping("/delete")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        relationService.delete(request.getId());
        return CommonResponse.success("删除成功");
    }

    @ApiOperation("批量删除 API与系统关系配置")
    @PostMapping("/batchDelete")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        relationService.batchDelete(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量删除成功");
    }

    @ApiOperation("批量启用 API与系统关系配置")
    @PostMapping("/batchEnable")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        relationService.batchEnable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量启用成功");
    }

    @ApiOperation("批量禁用 API与系统关系配置")
    @PostMapping("/batchDisable")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        relationService.batchDisable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量禁用成功");
    }
}
