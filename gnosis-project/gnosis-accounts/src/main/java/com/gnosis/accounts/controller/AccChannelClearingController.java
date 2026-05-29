package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.clearing.AccChannelClearingCreateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingIdsRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingQueryRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingUpdateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingVO;
import com.gnosis.accounts.service.AccChannelClearingService;
import com.gnosis.accounts.service.AccImportExportService;
import com.gnosis.common.dto.CommonResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
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
    public CommonResponse<PageResult<AccChannelClearingVO>> pageList(@RequestBody PageRequest<AccChannelClearingQueryRequest> request) {
        return CommonResponse.success(clearingService.pageList(request));
    }

    @ApiOperation("查询清算详情")
    @PostMapping("/detail")
    public CommonResponse<AccChannelClearingVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return CommonResponse.success(clearingService.detail(id));
    }

    @ApiOperation("新建清算")
    @PostMapping("/create")
    public CommonResponse<Void> create(@RequestBody AccChannelClearingCreateRequest request) {
        clearingService.create(request);
        return CommonResponse.success();
    }

    @ApiOperation("编辑清算")
    @PostMapping("/update")
    public CommonResponse<Void> update(@RequestBody AccChannelClearingUpdateRequest request) {
        clearingService.update(request);
        return CommonResponse.success();
    }

    @ApiOperation("删除清算")
    @PostMapping("/delete")
    public CommonResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        clearingService.delete(id);
        return CommonResponse.success();
    }

    @ApiOperation("批量删除清算")
    @PostMapping("/batchDelete")
    public CommonResponse<Void> batchDelete(@RequestBody AccChannelClearingIdsRequest request) {
        clearingService.batchDelete(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量启用清算")
    @PostMapping("/batchEnable")
    public CommonResponse<Void> batchEnable(@RequestBody AccChannelClearingIdsRequest request) {
        clearingService.batchEnable(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量禁用清算")
    @PostMapping("/batchDisable")
    public CommonResponse<Void> batchDisable(@RequestBody AccChannelClearingIdsRequest request) {
        clearingService.batchDisable(request);
        return CommonResponse.success();
    }

    @ApiOperation("执行清算")
    @PostMapping("/executeClearing")
    public CommonResponse<Void> executeClearing(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String operatorId = request.get("operatorId");
        clearingService.executeClearing(id, operatorId);
        return CommonResponse.success();
    }

    @ApiOperation("导出清算")
    @PostMapping("/export")
    public void export(@RequestBody AccChannelClearingIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportSubjects(null, response);
    }

    @ApiOperation("导入清算")
    @PostMapping("/import")
    public CommonResponse<Void> importClearing(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importSubjects(file);
            return CommonResponse.success();
        } catch (Exception e) {
            return CommonResponse.error("导入失败: " + e.getMessage());
        }
    }
}
