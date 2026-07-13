package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.template.SignatureTemplateCreateRequest;
import com.gnosis.signature.dto.template.SignatureTemplateIdsRequest;
import com.gnosis.signature.dto.template.SignatureTemplateQueryRequest;
import com.gnosis.signature.dto.template.SignatureTemplateUpdateRequest;
import com.gnosis.signature.dto.template.SignatureTemplateVO;
import com.gnosis.signature.service.SignatureTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = "签章模板管理")
@RestController
@RequestMapping("/signature/template")
public class SignatureTemplateController {

    @Autowired
    private SignatureTemplateService templateService;

    @ApiOperation("分页查询模板列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<SignatureTemplateVO>> pageList(@RequestBody PageRequest<SignatureTemplateQueryRequest> request) {
        return ResponseUtil.ok(templateService.pageList(request));
    }

    @ApiOperation("查询模板详情")
    @PostMapping("/detail")
    public BaseResponse<SignatureTemplateVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(templateService.detail(id));
    }

    @ApiOperation("新建模板")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody SignatureTemplateCreateRequest request) {
        templateService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑模板")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody SignatureTemplateUpdateRequest request) {
        templateService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除模板")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        templateService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除模板")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureTemplateIdsRequest request) {
        templateService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用模板")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody SignatureTemplateIdsRequest request) {
        templateService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用模板")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody SignatureTemplateIdsRequest request) {
        templateService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出模板")
    @PostMapping("/export")
    public void export(@RequestBody SignatureTemplateIdsRequest request, HttpServletResponse response) throws IOException {
        // TODO: 实现导出逻辑
    }

    @ApiOperation("导入模板")
    @PostMapping("/import")
    public BaseResponse<Void> importTemplates(@RequestParam("file") MultipartFile file) {
        try {
            // TODO: 实现导入逻辑
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
