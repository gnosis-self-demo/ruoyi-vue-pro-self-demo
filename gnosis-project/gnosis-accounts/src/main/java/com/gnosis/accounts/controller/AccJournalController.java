package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.journal.AccJournalCreateRequest;
import com.gnosis.accounts.dto.journal.AccJournalIdsRequest;
import com.gnosis.accounts.dto.journal.AccJournalQueryRequest;
import com.gnosis.accounts.dto.journal.AccJournalVO;
import com.gnosis.accounts.service.AccJournalService;
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

@Api(tags = "会计流水管理")
@RestController
@RequestMapping("/accounts/journal")
public class AccJournalController {

    @Autowired
    private AccJournalService journalService;

    @Autowired
    private AccImportExportService importExportService;

    @ApiOperation("分页查询流水列表")
    @PostMapping("/page")
    public CommonResponse<PageResult<AccJournalVO>> pageList(@RequestBody PageRequest<AccJournalQueryRequest> request) {
        return CommonResponse.success(journalService.pageList(request));
    }

    @ApiOperation("查询流水详情")
    @PostMapping("/detail")
    public CommonResponse<AccJournalVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return CommonResponse.success(journalService.detail(id));
    }

    @ApiOperation("新建流水")
    @PostMapping("/create")
    public CommonResponse<Void> create(@RequestBody AccJournalCreateRequest request) {
        journalService.create(request);
        return CommonResponse.success();
    }

    @ApiOperation("删除流水")
    @PostMapping("/delete")
    public CommonResponse<Void> delete(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        journalService.delete(id);
        return CommonResponse.success();
    }

    @ApiOperation("批量删除流水")
    @PostMapping("/batchDelete")
    public CommonResponse<Void> batchDelete(@RequestBody AccJournalIdsRequest request) {
        journalService.batchDelete(request);
        return CommonResponse.success();
    }

    @ApiOperation("过账")
    @PostMapping("/post")
    public CommonResponse<Void> post(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        journalService.post(id);
        return CommonResponse.success();
    }

    @ApiOperation("冲销")
    @PostMapping("/reverse")
    public CommonResponse<Void> reverse(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        journalService.reverse(id);
        return CommonResponse.success();
    }

    @ApiOperation("导出流水")
    @PostMapping("/export")
    public void export(@RequestBody AccJournalIdsRequest request, HttpServletResponse response) throws IOException {
        importExportService.exportJournals(request, response);
    }

    @ApiOperation("导入流水")
    @PostMapping("/import")
    public CommonResponse<Void> importJournals(@RequestParam("file") MultipartFile file) {
        try {
            importExportService.importJournals(file);
            return CommonResponse.success();
        } catch (Exception e) {
            return CommonResponse.error("导入失败: " + e.getMessage());
        }
    }
}
