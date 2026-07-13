package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.process.SignatureProcessCreateRequest;
import com.gnosis.signature.dto.process.SignatureProcessIdsRequest;
import com.gnosis.signature.dto.process.SignatureProcessQueryRequest;
import com.gnosis.signature.dto.process.SignatureProcessUpdateRequest;
import com.gnosis.signature.dto.process.SignatureProcessVO;
import com.gnosis.signature.service.SignatureProcessService;
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

@Api(tags = "签章流程管理")
@RestController
@RequestMapping("/signature/process")
public class SignatureProcessController {

    @Autowired
    private SignatureProcessService processService;

    @ApiOperation("分页查询流程列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<SignatureProcessVO>> pageList(@RequestBody PageRequest<SignatureProcessQueryRequest> request) {
        return ResponseUtil.ok(processService.pageList(request));
    }

    @ApiOperation("查询流程详情")
    @PostMapping("/detail")
    public BaseResponse<SignatureProcessVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(processService.detail(id));
    }

    @ApiOperation("新建流程")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody SignatureProcessCreateRequest request) {
        processService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑流程")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody SignatureProcessUpdateRequest request) {
        processService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除流程")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        processService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除流程")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureProcessIdsRequest request) {
        processService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用流程")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody SignatureProcessIdsRequest request) {
        processService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用流程")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody SignatureProcessIdsRequest request) {
        processService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出流程")
    @PostMapping("/export")
    public void export(@RequestBody SignatureProcessIdsRequest request, HttpServletResponse response) throws IOException {
        // TODO: 实现导出逻辑
    }

    @ApiOperation("导入流程")
    @PostMapping("/import")
    public BaseResponse<Void> importProcesses(@RequestParam("file") MultipartFile file) {
        try {
            // TODO: 实现导入逻辑
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
