package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.audit.SignatureAuditLogIdsRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogQueryRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogVO;
import com.gnosis.signature.service.SignatureAuditLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation("批量删除审计日志")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureAuditLogIdsRequest request) {
        auditLogService.batchDelete(request);
        return ResponseUtil.ok();
    }
}
