package com.gnosis.signature.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.signature.dto.file.SignatureFileCreateRequest;
import com.gnosis.signature.dto.file.SignatureFileIdsRequest;
import com.gnosis.signature.dto.file.SignatureFileQueryRequest;
import com.gnosis.signature.dto.file.SignatureFileUpdateRequest;
import com.gnosis.signature.dto.file.SignatureFileVO;
import com.gnosis.signature.service.SignatureFileService;
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

@Api(tags = "签章文件管理")
@RestController
@RequestMapping("/signature/file")
public class SignatureFileController {

    @Autowired
    private SignatureFileService fileService;

    @ApiOperation("分页查询文件列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<SignatureFileVO>> pageList(@RequestBody PageRequest<SignatureFileQueryRequest> request) {
        return ResponseUtil.ok(fileService.pageList(request));
    }

    @ApiOperation("查询文件详情")
    @PostMapping("/detail")
    public BaseResponse<SignatureFileVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return ResponseUtil.ok(fileService.detail(id));
    }

    @ApiOperation("新建文件")
    @PostMapping("/create")
    public BaseResponse<Void> create(@RequestBody SignatureFileCreateRequest request) {
        fileService.create(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("编辑文件")
    @PostMapping("/update")
    public BaseResponse<Void> update(@RequestBody SignatureFileUpdateRequest request) {
        fileService.update(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("删除文件")
    @PostMapping("/delete")
    public BaseResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        fileService.delete(id);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量删除文件")
    @PostMapping("/batchDelete")
    public BaseResponse<Void> batchDelete(@RequestBody SignatureFileIdsRequest request) {
        fileService.batchDelete(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量启用文件")
    @PostMapping("/batchEnable")
    public BaseResponse<Void> batchEnable(@RequestBody SignatureFileIdsRequest request) {
        fileService.batchEnable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("批量禁用文件")
    @PostMapping("/batchDisable")
    public BaseResponse<Void> batchDisable(@RequestBody SignatureFileIdsRequest request) {
        fileService.batchDisable(request);
        return ResponseUtil.ok();
    }

    @ApiOperation("导出文件")
    @PostMapping("/export")
    public void export(@RequestBody SignatureFileIdsRequest request, HttpServletResponse response) throws IOException {
        // TODO: 实现导出逻辑
    }

    @ApiOperation("导入文件")
    @PostMapping("/import")
    public BaseResponse<Void> importFiles(@RequestParam("file") MultipartFile file) {
        try {
            // TODO: 实现导入逻辑
            return ResponseUtil.ok();
        } catch (Exception e) {
            return ResponseUtil.fail("导入失败: " + e.getMessage());
        }
    }
}
