package com.gnosis.accounts.controller;

import com.gnosis.accounts.dto.entry.AccEntryQueryRequest;
import com.gnosis.accounts.dto.entry.AccEntryVO;
import com.gnosis.accounts.service.AccEntryService;
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

import java.util.List;
import java.util.Map;

@Api(tags = "会计分录管理")
@RestController
@RequestMapping("/accounts/entry")
public class AccEntryController {

    @Autowired
    private AccEntryService entryService;

    @ApiOperation("分页查询分录列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<AccEntryVO>> pageList(@RequestBody PageRequest<AccEntryQueryRequest> request) {
        return BaseResponse.success(entryService.pageList(request));
    }

    @ApiOperation("查询分录详情")
    @PostMapping("/detail")
    public BaseResponse<AccEntryVO> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return BaseResponse.success(entryService.detail(id));
    }

    @ApiOperation("根据流水ID查询分录列表")
    @PostMapping("/listByJournal")
    public BaseResponse<List<AccEntryVO>> listByJournal(@RequestBody Map<String, String> request) {
        String journalId = request.get("journalId");
        return BaseResponse.success(entryService.selectByJournalId(journalId));
    }
}
