package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.supplier.SignatureSupplierCreateRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierIdsRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierQueryRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierUpdateRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierVO;
import com.gnosis.signature.service.SignatureSupplierService;
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

@Api(tags = "供应商管理")
@RestController
@RequestMapping("/signature/supplier")
public class SignatureSupplierController {

    @Autowired
    private SignatureSupplierService supplierService;

    @ApiOperation("分页查询供应商列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<SignatureSupplierVO>> pageList(@RequestBody PageRequest<SignatureSupplierQueryRequest> request) {
        return ResponseUtil.ok(supplierService.pageList(request));
    }

    @ApiOperation("查询供应商详情")
    @PostMapping("/detail")
    public BaseResponse<SignatureSupplierVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(supplierService.detail(id));
    }

    @ApiOperation("新建供应商")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody SignatureSupplierCreateRequest request) {
        supplierService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑供应商")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody SignatureSupplierUpdateRequest request) {
        supplierService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除供应商")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        supplierService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除供应商")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureSupplierIdsRequest request) {
        supplierService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用供应商")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody SignatureSupplierIdsRequest request) {
        supplierService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用供应商")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody SignatureSupplierIdsRequest request) {
        supplierService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出供应商")
    @PostMapping("/export")
    public void export(@RequestBody SignatureSupplierIdsRequest request, HttpServletResponse response) throws IOException {
        // TODO: 实现导出逻辑
    }

    @ApiOperation("导入供应商")
    @PostMapping("/import")
    public BaseResponse<Void> importSuppliers(@RequestParam("file") MultipartFile file) {
        try {
            // TODO: 实现导入逻辑
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
