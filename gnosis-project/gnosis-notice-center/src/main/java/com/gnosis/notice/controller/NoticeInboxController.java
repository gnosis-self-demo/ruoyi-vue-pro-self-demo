package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.config.NoticeContextUtil;
import com.gnosis.notice.dto.inbox.NoticeInboxCreateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxQueryRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxUpdateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxVO;
import com.gnosis.notice.service.NoticeInboxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 站内信管理接口 - 修复版: userId从上下文获取 + 编辑修改/导入导出/批量启用禁用
 */
@Api(tags = "站内信管理")
@RestController
@RequestMapping("/inbox")
public class NoticeInboxController {

    @Autowired
    private NoticeInboxService inboxService;

    @Autowired
    private NoticeContextUtil contextUtil;

    @ApiOperation("分页查询站内信列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<NoticeInboxVO>> pageList(@RequestBody NoticeInboxQueryRequest request) {
        return ResponseUtil.ok(inboxService.pageList(request));
    }

    @ApiOperation("查询站内信详情")
    @PostMapping("/detail")
    public BaseResponse<NoticeInboxVO> detail(@RequestBody NoticeInboxVO request) {
        NoticeInboxVO vo = inboxService.detail(request.getId());
        return ResponseUtil.ok(vo);
    }

    @ApiOperation("统计未读数量")
    @PostMapping("/countUnread")
    public BaseResponse<Long> countUnread(@RequestBody NoticeInboxQueryRequest request) {
        Long count = inboxService.countUnread(request.getUserId());
        return ResponseUtil.ok(count);
    }

    @ApiOperation("创建站内信")
    @PostMapping("/create")
    public BaseResponse<Integer> create(@RequestBody NoticeInboxCreateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        int result = inboxService.create(request, userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("编辑修改站内信")
    @PostMapping("/update")
    public BaseResponse<Integer> update(@RequestBody NoticeInboxUpdateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        int result = inboxService.update(request, userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("标记已读")
    @PostMapping("/markRead")
    public BaseResponse<Integer> markRead(@RequestBody NoticeInboxVO request) {
        int result = inboxService.markRead(request.getId());
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量标记已读")
    @PostMapping("/batchRead")
    public BaseResponse<Integer> batchMarkRead(@RequestBody List<String> ids) {
        int result = inboxService.batchMarkRead(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量启用(恢复)站内信")
    @PostMapping("/batchEnable")
    public BaseResponse<Integer> batchEnable(@RequestBody List<String> ids) {
        int result = inboxService.batchEnable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量禁用(归档)站内信")
    @PostMapping("/batchDisable")
    public BaseResponse<Integer> batchDisable(@RequestBody List<String> ids) {
        int result = inboxService.batchDisable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("删除站内信")
    @PostMapping("/delete")
    public BaseResponse<Integer> delete(@RequestBody NoticeInboxVO request) {
        int result = inboxService.delete(request.getId());
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量删除站内信")
    @PostMapping("/batchDelete")
    public BaseResponse<Integer> batchDelete(@RequestBody List<String> ids) {
        int result = inboxService.batchDelete(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("导出站内信")
    @PostMapping("/export")
    public void export(@RequestBody NoticeInboxQueryRequest request, HttpServletResponse response) throws Exception {
        inboxService.export(request, response);
    }

    @ApiOperation("导入站内信")
    @PostMapping("/import")
    public BaseResponse<Integer> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        String userId = contextUtil.getCurrentUserId();
        int count = inboxService.importExcel(file.getInputStream(), userId);
        return ResponseUtil.ok(count);
    }
}
