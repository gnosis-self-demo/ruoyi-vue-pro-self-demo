package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.certificate.SignatureCertificateCreateRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateIdsRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateQueryRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateUpdateRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateVO;
import com.gnosis.signature.service.SignatureCertificateService;
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

@Api(tags = "证书管理")
@RestController
@RequestMapping("/signature/certificate")
public class SignatureCertificateController {

    @Autowired
    private SignatureCertificateService certificateService;

    @ApiOperation("分页查询证书列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<SignatureCertificateVO>> pageList(@RequestBody PageRequest<SignatureCertificateQueryRequest> request) {
        return ResponseUtil.ok(certificateService.pageList(request));
    }

    @ApiOperation("查询证书详情")
    @PostMapping("/detail")
    public BaseResponse<SignatureCertificateVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(certificateService.detail(id));
    }

    @ApiOperation("新建证书")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody SignatureCertificateCreateRequest request) {
        certificateService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑证书")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody SignatureCertificateUpdateRequest request) {
        certificateService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除证书")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        certificateService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除证书")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureCertificateIdsRequest request) {
        certificateService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用证书")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody SignatureCertificateIdsRequest request) {
        certificateService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用证书")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody SignatureCertificateIdsRequest request) {
        certificateService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出证书")
    @PostMapping("/export")
    public void export(@RequestBody SignatureCertificateIdsRequest request, HttpServletResponse response) throws IOException {
        certificateService.exportData(request, response);
    }

    @ApiOperation("导入证书")
    @PostMapping("/import")
    public BaseResponse<Void> importCertificates(@RequestParam("file") MultipartFile file) {
        try {
            certificateService.importData(file);
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
