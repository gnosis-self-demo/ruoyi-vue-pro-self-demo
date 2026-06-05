package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.transfer.AccBankTransferCreateRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferIdsRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferQueryRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferUpdateRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferVO;
import com.gnosis.accounts.service.AccBankTransferService;
import com.gnosis.accounts.service.AccImportExportService;
import com.gnosis.common.dto.BaseResponse;
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

@Api(tags = "银存结转管理")
@RestController
@RequestMapping("/accounts/transfer")
public class AccBankTransferController {

    @Autowired
    private AccBankTransferService transferService;

    @Autowired
    private AccImportExportService importExportService;

    @ApiOperation("分页查询结转列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<AccBankTransferVO>> pageList(@RequestBody PageRequest<AccBankTransferQueryRequest> request) {
        return BaseResponse.success(transferService.pageList(request));
    }

    @ApiOperation("查询结转详情")
    @PostMapping("/detail")
    public BaseResponse<AccBankTransferVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return BaseResponse.success(transferService.detail(id));
    }

    @ApiOperation("新建结转")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody AccBankTransferCreateRequest request) {
        transferService.create(request);
        return BaseResponse.success();
    }

    @ApiOperation("编辑结转")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody AccBankTransferUpdateRequest request) {
        transferService.update(request);
        return BaseResponse.success();
    }

    @ApiOperation("删除结转")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        transferService.delete(id);
        return BaseResponse.success();
    }

    @ApiOperation("批量删除结转")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody AccBankTransferIdsRequest request) {
        transferService.batchDelete(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量启用结转")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody AccBankTransferIdsRequest request) {
        transferService.batchEnable(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量禁用结转")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody AccBankTransferIdsRequest request) {
        transferService.batchDisable(request);
        return BaseResponse.success();
    }

    @ApiOperation("执行结转")
    @PostMapping("/executeTransfer")
    public BaseResponse<Void> executeTransfer(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String operatorId = request.get("operatorId");
        transferService.executeTransfer(id, operatorId);
        return BaseResponse.success();
    }

    @ApiOperation("导出结转")
    @PostMapping("/export")
    public void export(@RequestBody AccBankTransferIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportSubjects(null, response);
    }

    @ApiOperation("导入结转")
    @PostMapping("/import")
    public BaseResponse<Void> importTransfer(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importSubjects(file);
            return BaseResponse.success();
        } catch (Exception e) {
            return BaseResponse.error("导入失败: " + e.getMessage());
        }
    }
}
