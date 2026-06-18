package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.clearing.AccChannelClearingCreateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingIdsRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingQueryRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingUpdateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingVO;
import com.gnosis.accounts.service.AccChannelClearingService;
import com.gnosis.accounts.service.AccImportExportService;
import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Api(tags = "渠道清算管理")
@RestController
@RequestMapping("/accounts/clearing")
public class AccChannelClearingController {

    @Autowired
    private AccChannelClearingService clearingService;

    @Autowired
    private AccImportExportService importExportService;

    @ApiOperation("分页查询清算列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<AccChannelClearingVO>> pageList(@RequestBody PageRequest<AccChannelClearingQueryRequest> request) {
        return ResponseUtil.ok(clearingService.pageList(request));
    }

    @ApiOperation("查询清算详情")
    @PostMapping("/detail")
    public BaseResponse<AccChannelClearingVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(clearingService.detail(id));
    }

    @ApiOperation("新建清算")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody AccChannelClearingCreateRequest request) {
        clearingService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑清算")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody AccChannelClearingUpdateRequest request) {
        clearingService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除清算")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        clearingService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除清算")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody AccChannelClearingIdsRequest request) {
        clearingService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用清算")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody AccChannelClearingIdsRequest request) {
        clearingService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用清算")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody AccChannelClearingIdsRequest request) {
        clearingService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("执行清算")
    @PostMapping("/executeClearing")
    public BaseResponse<Void> executeClearing(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String operatorId = request.get("operatorId");
        clearingService.executeClearing(id, operatorId);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出清算")
    @PostMapping("/export")
    public void export(@RequestBody AccChannelClearingIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportClearings(request, response);
    }

    @ApiOperation("导入清算")
    @PostMapping("/import")
    public BaseResponse<Void> importClearing(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importClearings(file);
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
