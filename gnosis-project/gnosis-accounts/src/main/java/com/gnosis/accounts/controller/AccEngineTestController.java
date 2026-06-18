package com.gnosis.accounts.controller;

import com.gnosis.accounts.api.AccountsApi;
import com.gnosis.accounts.api.EntryItem;
import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "账务引擎测试")
@RestController
@RequestMapping("/accounts/engine")
public class AccEngineTestController {

    @Autowired
    private AccountsApi accountsApi;

    @ApiOperation("收单记账")
    @PostMapping("/collectionEntry")
    public BaseResponse<String> collectionEntry(@RequestBody Map<String, Object> params) {
        String channelCode = (String) params.get("channelCode");
        String customerId = (String) params.get("customerId");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        BigDecimal fee = params.get("fee") != null ? new BigDecimal(params.get("fee").toString()) : BigDecimal.ZERO;
        String businessId = (String) params.get("businessId");
        String operatorId = (String) params.get("operatorId");
        String journalId = accountsApi.collectionEntry(channelCode, customerId, amount, fee, businessId, operatorId);
        return ResponseUtil.ok(journalId);
    }

    @ApiOperation("退款记账")
    @PostMapping("/refundEntry")
    public BaseResponse<String> refundEntry(@RequestBody Map<String, Object> params) {
        String channelCode = (String) params.get("channelCode");
        String customerId = (String) params.get("customerId");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        String businessId = (String) params.get("businessId");
        String operatorId = (String) params.get("operatorId");
        String journalId = accountsApi.refundEntry(channelCode, customerId, amount, businessId, operatorId);
        return ResponseUtil.ok(journalId);
    }

    @ApiOperation("付款记账")
    @PostMapping("/paymentEntry")
    public BaseResponse<String> paymentEntry(@RequestBody Map<String, Object> params) {
        String channelCode = (String) params.get("channelCode");
        String customerId = (String) params.get("customerId");
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        BigDecimal fee = params.get("fee") != null ? new BigDecimal(params.get("fee").toString()) : BigDecimal.ZERO;
        String businessId = (String) params.get("businessId");
        String operatorId = (String) params.get("operatorId");
        String journalId = accountsApi.paymentEntry(channelCode, customerId, amount, fee, businessId, operatorId);
        return ResponseUtil.ok(journalId);
    }

    @ApiOperation("渠道清算")
    @PostMapping("/channelClearing")
    public BaseResponse<String> channelClearing(@RequestBody Map<String, Object> params) {
        String channelCode = (String) params.get("channelCode");
        Date clearingDate = new Date();
        String operatorId = (String) params.get("operatorId");
        String journalId = accountsApi.executeChannelClearing(channelCode, clearingDate, operatorId);
        return ResponseUtil.ok(journalId);
    }

    @ApiOperation("银存结转")
    @PostMapping("/bankTransfer")
    public BaseResponse<String> bankTransfer(@RequestBody Map<String, Object> params) {
        String channelCode = (String) params.get("channelCode");
        Date transferDate = new Date();
        String operatorId = (String) params.get("operatorId");
        String journalId = accountsApi.executeBankTransfer(channelCode, transferDate, operatorId);
        return ResponseUtil.ok(journalId);
    }

    @ApiOperation("客资结算")
    @PostMapping("/customerSettlement")
    public BaseResponse<String> customerSettlement(@RequestBody Map<String, Object> params) {
        String customerId = (String) params.get("customerId");
        Date settlementDate = new Date();
        String operatorId = (String) params.get("operatorId");
        String journalId = accountsApi.executeCustomerSettlement(customerId, settlementDate, operatorId);
        return ResponseUtil.ok(journalId);
    }

    @ApiOperation("查询账户余额")
    @PostMapping("/getAccountBalance")
    public BaseResponse<BigDecimal> getAccountBalance(@RequestBody Map<String, String> params) {
        BigDecimal balance = accountsApi.getAccountBalance(params.get("accountId"));
        return ResponseUtil.ok(balance);
    }

    @ApiOperation("查询客户余额")
    @PostMapping("/getCustomerBalance")
    public BaseResponse<BigDecimal> getCustomerBalance(@RequestBody Map<String, String> params) {
        BigDecimal balance = accountsApi.getCustomerBalance(params.get("customerId"));
        return ResponseUtil.ok(balance);
    }

    @ApiOperation("查询渠道待清算金额")
    @PostMapping("/getChannelPendingAmount")
    public BaseResponse<BigDecimal> getChannelPendingAmount(@RequestBody Map<String, String> params) {
        BigDecimal amount = accountsApi.getChannelPendingAmount(params.get("channelCode"));
        return ResponseUtil.ok(amount);
    }
}