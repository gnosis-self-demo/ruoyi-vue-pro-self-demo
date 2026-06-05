package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.settlement.AccCustomerSettlementCreateRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementIdsRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementQueryRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementUpdateRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementVO;
import com.gnosis.accounts.service.AccCustomerSettlementService;
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

@Api(tags = "客资结算管理")
@RestController
@RequestMapping("/accounts/settlement")
public class AccCustomerSettlementController {

    @Autowired
    private AccCustomerSettlementService settlementService;

    @Autowired
    private AccImportExportService importExportService;

    @ApiOperation("分页查询结算列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<AccCustomerSettlementVO>> pageList(@RequestBody PageRequest<AccCustomerSettlementQueryRequest> request) {
        return BaseResponse.success(settlementService.pageList(request));
    }

    @ApiOperation("查询结算详情")
    @PostMapping("/detail")
    public BaseResponse<AccCustomerSettlementVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return BaseResponse.success(settlementService.detail(id));
    }

    @ApiOperation("新建结算")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody AccCustomerSettlementCreateRequest request) {
        settlementService.create(request);
        return BaseResponse.success();
    }

    @ApiOperation("编辑结算")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody AccCustomerSettlementUpdateRequest request) {
        settlementService.update(request);
        return BaseResponse.success();
    }

    @ApiOperation("删除结算")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        settlementService.delete(id);
        return BaseResponse.success();
    }

    @ApiOperation("批量删除结算")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody AccCustomerSettlementIdsRequest request) {
        settlementService.batchDelete(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量启用结算")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody AccCustomerSettlementIdsRequest request) {
        settlementService.batchEnable(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量禁用结算")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody AccCustomerSettlementIdsRequest request) {
        settlementService.batchDisable(request);
        return BaseResponse.success();
    }

    @ApiOperation("执行结算")
    @PostMapping("/executeSettlement")
    public BaseResponse<Void> executeSettlement(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String operatorId = request.get("operatorId");
        settlementService.executeSettlement(id, operatorId);
        return BaseResponse.success();
    }

    @ApiOperation("导出结算")
    @PostMapping("/export")
    public void export(@RequestBody AccCustomerSettlementIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportSubjects(null, response);
    }

    @ApiOperation("导入结算")
    @PostMapping("/import")
    public BaseResponse<Void> importSettlement(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importSubjects(file);
            return BaseResponse.success();
        } catch (Exception e) {
            return BaseResponse.error("导入失败: " + e.getMessage());
        }
    }
}
