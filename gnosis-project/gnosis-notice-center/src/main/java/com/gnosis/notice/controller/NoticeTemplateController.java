package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.dto.template.NoticeTemplateCreateRequest;
import com.gnosis.notice.dto.template.NoticeTemplateQueryRequest;
import com.gnosis.notice.dto.template.NoticeTemplateUpdateRequest;
import com.gnosis.notice.dto.template.NoticeTemplateVO;
import com.gnosis.notice.service.NoticeTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 消息模板管理接口
 */
@Api(tags = "消息模板管理")
@RestController
@RequestMapping("/template")
public class NoticeTemplateController {

    @Autowired
    private NoticeTemplateService templateService;

    @ApiOperation("分页查询模板列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<NoticeTemplateVO>> pageList(@RequestBody NoticeTemplateQueryRequest request) {
        return ResponseUtil.ok(templateService.pageList(request));
    }

    @ApiOperation("查询模板详情")
    @PostMapping("/detail")
    public BaseResponse<NoticeTemplateVO> detail(@RequestBody NoticeTemplateVO request) {
        NoticeTemplateVO vo = templateService.detail(request.getId());
        return ResponseUtil.ok(vo);
    }

    @ApiOperation("创建模板")
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody NoticeTemplateCreateRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        String id = templateService.create(request, userId);
        return ResponseUtil.ok(id);
    }

    @ApiOperation("更新模板")
    @PostMapping("/update")
    public BaseResponse<Integer> update(@RequestBody NoticeTemplateUpdateRequest request) {
        String userId = "admin"; // TODO: 从登录上下文获取
        int result = templateService.update(request, userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("删除模板")
    @PostMapping("/delete")
    public BaseResponse<Integer> delete(@RequestBody NoticeTemplateVO request) {
        int result = templateService.delete(request.getId());
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量删除模板")
    @PostMapping("/batchDelete")
    public BaseResponse<Integer> batchDelete(@RequestBody List<String> ids) {
        int result = templateService.batchDelete(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量启用模板")
    @PostMapping("/batchEnable")
    public BaseResponse<Integer> batchEnable(@RequestBody List<String> ids) {
        int result = templateService.batchEnable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量禁用模板")
    @PostMapping("/batchDisable")
    public BaseResponse<Integer> batchDisable(@RequestBody List<String> ids) {
        int result = templateService.batchDisable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("导出模板")
    @PostMapping("/export")
    public void export(@RequestBody NoticeTemplateQueryRequest request, HttpServletResponse response) throws Exception {
        templateService.export(request, response);
    }

    @ApiOperation("导入模板")
    @PostMapping("/import")
    public BaseResponse<Integer> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        String userId = "admin"; // TODO: 从登录上下文获取
        int count = templateService.importExcel(file.getInputStream(), userId);
        return ResponseUtil.ok(count);
    }
}
