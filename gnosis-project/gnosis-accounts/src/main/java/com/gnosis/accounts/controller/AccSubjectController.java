package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.subject.AccSubjectCreateRequest;
import com.gnosis.accounts.dto.subject.AccSubjectIdsRequest;
import com.gnosis.accounts.dto.subject.AccSubjectQueryRequest;
import com.gnosis.accounts.dto.subject.AccSubjectUpdateRequest;
import com.gnosis.accounts.dto.subject.AccSubjectVO;
import com.gnosis.accounts.service.AccSubjectService;
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

@Api(tags = "会计科目管理")
@RestController
@RequestMapping("/accounts/subject")
public class AccSubjectController {

    @Autowired
    private AccSubjectService subjectService;

    @Autowired
    private AccImportExportService importExportService;

    @ApiOperation("分页查询科目列表")
    @PostMapping("/page")
    public CommonResponse<PageResult<AccSubjectVO>> pageList(@RequestBody PageRequest<AccSubjectQueryRequest> request) {
        return CommonResponse.success(subjectService.pageList(request));
    }

    @ApiOperation("查询科目详情")
    @PostMapping("/detail")
    public CommonResponse<AccSubjectVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return CommonResponse.success(subjectService.detail(id));
    }

    @ApiOperation("新建科目")
    @PostMapping("/create")
    public CommonResponse<Void> create(@RequestBody AccSubjectCreateRequest request) {
        subjectService.create(request);
        return CommonResponse.success();
    }

    @ApiOperation("编辑科目")
    @PostMapping("/update")
    public CommonResponse<Void> update(@RequestBody AccSubjectUpdateRequest request) {
        subjectService.update(request);
        return CommonResponse.success();
    }

    @ApiOperation("删除科目")
    @PostMapping("/delete")
    public CommonResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        subjectService.delete(id);
        return CommonResponse.success();
    }

    @ApiOperation("批量删除科目")
    @PostMapping("/batchDelete")
    public CommonResponse<Void> batchDelete(@RequestBody AccSubjectIdsRequest request) {
        subjectService.batchDelete(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量启用科目")
    @PostMapping("/batchEnable")
    public CommonResponse<Void> batchEnable(@RequestBody AccSubjectIdsRequest request) {
        subjectService.batchEnable(request);
        return CommonResponse.success();
    }

    @ApiOperation("批量禁用科目")
    @PostMapping("/batchDisable")
    public CommonResponse<Void> batchDisable(@RequestBody AccSubjectIdsRequest request) {
        subjectService.batchDisable(request);
        return CommonResponse.success();
    }

    @ApiOperation("导出科目")
    @PostMapping("/export")
    public void export(@RequestBody AccSubjectIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportSubjects(request, response);
    }

    @ApiOperation("导入科目")
    @PostMapping("/import")
    public CommonResponse<Void> importSubjects(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importSubjects(file);
            return CommonResponse.success();
        } catch (Exception e) {
            return CommonResponse.error("导入失败: " + e.getMessage());
        }
    }
}
