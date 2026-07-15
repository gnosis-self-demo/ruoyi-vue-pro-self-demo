package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.seal.SignatureSealCreateRequest;
import com.gnosis.signature.dto.seal.SignatureSealIdsRequest;
import com.gnosis.signature.dto.seal.SignatureSealQueryRequest;
import com.gnosis.signature.dto.seal.SignatureSealUpdateRequest;
import com.gnosis.signature.dto.seal.SignatureSealVO;
import com.gnosis.signature.service.SignatureSealService;
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

@Api(tags = "印章管理")
@RestController
@RequestMapping("/signature/seal")
public class SignatureSealController {

    @Autowired
    private SignatureSealService sealService;

    @ApiOperation("分页查询印章列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<SignatureSealVO>> pageList(@RequestBody PageRequest<SignatureSealQueryRequest> request) {
        return ResponseUtil.ok(sealService.pageList(request));
    }

    @ApiOperation("查询印章详情")
    @PostMapping("/detail")
    public BaseResponse<SignatureSealVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(sealService.detail(id));
    }

    @ApiOperation("新建印章")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody SignatureSealCreateRequest request) {
        sealService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑印章")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody SignatureSealUpdateRequest request) {
        sealService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除印章")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        sealService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除印章")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureSealIdsRequest request) {
        sealService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用印章")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody SignatureSealIdsRequest request) {
        sealService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用印章")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody SignatureSealIdsRequest request) {
        sealService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出印章")
    @PostMapping("/export")
    public void export(@RequestBody SignatureSealIdsRequest request, HttpServletResponse response) throws IOException {
        sealService.exportData(request, response);
    }

    @ApiOperation("导入印章")
    @PostMapping("/import")
    public BaseResponse<Void> importSeals(@RequestParam("file") MultipartFile file) {
        try {
            sealService.importData(file);
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
