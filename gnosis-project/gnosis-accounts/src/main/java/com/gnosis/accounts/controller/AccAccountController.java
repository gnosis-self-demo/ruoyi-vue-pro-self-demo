package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.account.AccAccountCreateRequest;
import com.gnosis.accounts.dto.account.AccAccountIdsRequest;
import com.gnosis.accounts.dto.account.AccAccountQueryRequest;
import com.gnosis.accounts.dto.account.AccAccountUpdateRequest;
import com.gnosis.accounts.dto.account.AccAccountVO;
import com.gnosis.accounts.service.AccAccountService;
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
import java.math.BigDecimal;
import java.util.Map;

@Api(tags = "账户管理")
@RestController
@RequestMapping("/accounts/account")
public class AccAccountController {

    @Autowired
    private AccAccountService accountService;

    @Autowired
    private AccImportExportService importExportService;

    @ApiOperation("分页查询账户列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<AccAccountVO>> pageList(@RequestBody PageRequest<AccAccountQueryRequest> request) {
        return BaseResponse.success(accountService.pageList(request));
    }

    @ApiOperation("查询账户详情")
    @PostMapping("/detail")
    public BaseResponse<AccAccountVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return BaseResponse.success(accountService.detail(id));
    }

    @ApiOperation("新建账户")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody AccAccountCreateRequest request) {
        accountService.create(request);
        return BaseResponse.success();
    }

    @ApiOperation("编辑账户")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody AccAccountUpdateRequest request) {
        accountService.update(request);
        return BaseResponse.success();
    }

    @ApiOperation("删除账户")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        accountService.delete(id);
        return BaseResponse.success();
    }

    @ApiOperation("批量删除账户")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody AccAccountIdsRequest request) {
        accountService.batchDelete(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量启用账户")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody AccAccountIdsRequest request) {
        accountService.batchEnable(request);
        return BaseResponse.success();
    }

    @ApiOperation("批量禁用账户")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody AccAccountIdsRequest request) {
        accountService.batchDisable(request);
        return BaseResponse.success();
    }

    @ApiOperation("查询账户余额")
    @PostMapping("/balance")
    public BaseResponse<BigDecimal> getBalance(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return BaseResponse.success(accountService.getAccountBalance(id));
    }

    @ApiOperation("导出账户")
    @PostMapping("/export")
    public void export(@RequestBody AccAccountIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportAccounts(request, response);
    }

    @ApiOperation("导入账户")
    @PostMapping("/import")
    public BaseResponse<Void> importAccounts(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importAccounts(file);
            return BaseResponse.success();
        } catch (Exception e) {
            return BaseResponse.error("导入失败: " + e.getMessage());
        }
    }
}
