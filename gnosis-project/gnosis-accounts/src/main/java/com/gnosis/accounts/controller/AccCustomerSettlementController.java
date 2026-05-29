package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.settlement.AccCustomerSettlementCreateRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementIdsRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementQueryRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementUpdateRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementVO;
import com.gnosis.accounts.service.AccCustomerSettlementService;
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
    public CommonResponse<PageResult<AccCustomerSettlementVO>> pageList(@RequestBody PageRequest<AccCustomerSettlementQueryRequest> request) {
        return CommonResponse.success(settlementService.pageList(request));
    }

    @ApiOperation("查询结算详情")
    @PostMapping("/detail")
    public CommonResponse<AccCustomerSettlementVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return CommonResponse.success(settlementService.detail(id));
    }

    @ApiOperation("新建结算")
    @PostMapping("/create")
    public CommonResponse<Void> create(@RequestBody AccCustomerSettlementCreateRequest request) {
        settlementService.create(request);
        return CommonResponse.success();
    }

    @ApiOperation("编辑结算")
    @PostMapping("/update")
    public CommonResponse<Void> update(@RequestBody AccCustomerSettlementUpdateRequest request) {
        settlementService.update(request);
        return CommonResponse.success();
    }

    @ApiOperation("删除结算")
    @PostMapping("/delete")
    public CommonResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        settlementService.delete(id);
        return CommonResponse.success();
    }

    @ApiOperation("批量删除结算")
    @PostMapping("/batchDelete")
    public CommonResponse<Void> batchDelete(@RequestBody AccCustomerSettlementIdsRequest request) {
        settlementService.batchDelete(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量启用结算")
    @PostMapping("/batchEnable")
    public CommonResponse<Void> batchEnable(@RequestBody AccCustomerSettlementIdsRequest request) {
        settlementService.batchEnable(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量禁用结算")
    @PostMapping("/batchDisable")
    public CommonResponse<Void> batchDisable(@RequestBody AccCustomerSettlementIdsRequest request) {
        settlementService.batchDisable(request);
        return CommonResponse.success();
    }

    @ApiOperation("执行结算")
    @PostMapping("/executeSettlement")
    public CommonResponse<Void> executeSettlement(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String operatorId = request.get("operatorId");
        settlementService.executeSettlement(id, operatorId);
        return CommonResponse.success();
    }

    @ApiOperation("导出结算")
    @PostMapping("/export")
    public void export(@RequestBody AccCustomerSettlementIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportSubjects(null, response);
    }

    @ApiOperation("导入结算")
    @PostMapping("/import")
    public CommonResponse<Void> importSettlement(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importSubjects(file);
            return CommonResponse.success();
        } catch (Exception e) {
            return CommonResponse.error("导入失败: " + e.getMessage());
        }
    }
}
