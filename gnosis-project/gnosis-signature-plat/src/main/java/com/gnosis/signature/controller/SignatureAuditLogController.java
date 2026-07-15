package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.audit.SignatureAuditLogCreateRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogIdsRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogQueryRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogUpdateRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogVO;
import com.gnosis.signature.service.SignatureAuditLogService;
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

@Api(tags = "审计日志管理")
@RestController
@RequestMapping("/signature/audit-log")
public class SignatureAuditLogController {

    @Autowired
    private SignatureAuditLogService auditLogService;

    @ApiOperation("分页查询审计日志列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<SignatureAuditLogVO>> pageList(@RequestBody PageRequest<SignatureAuditLogQueryRequest> request) {
        return ResponseUtil.ok(auditLogService.pageList(request));
    }

    @ApiOperation("查询审计日志详情")
    @PostMapping("/detail")
    public BaseResponse<SignatureAuditLogVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(auditLogService.detail(id));
    }

    @ApiOperation("新建审计日志")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody SignatureAuditLogCreateRequest request) {
        auditLogService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑审计日志")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody SignatureAuditLogUpdateRequest request) {
        auditLogService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除审计日志")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        auditLogService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除审计日志")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureAuditLogIdsRequest request) {
        auditLogService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用审计日志")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody SignatureAuditLogIdsRequest request) {
        auditLogService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用审计日志")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody SignatureAuditLogIdsRequest request) {
        auditLogService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出审计日志")
    @PostMapping("/export")
    public void export(@RequestBody SignatureAuditLogIdsRequest request, HttpServletResponse response) throws IOException {
        auditLogService.exportData(request, response);
    }

    @ApiOperation("导入审计日志")
    @PostMapping("/import")
    public BaseResponse<Void> importData(@RequestParam("file") MultipartFile file) {
        try {
            auditLogService.importData(file);
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
