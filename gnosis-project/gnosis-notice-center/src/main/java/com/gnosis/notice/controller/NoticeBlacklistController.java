package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.config.NoticeContextUtil;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistCreateRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistQueryRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistUpdateRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistVO;
import com.gnosis.notice.service.NoticeBlacklistService;
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
import java.util.List;

/**
 * 消息黑名单管理接口
 */
@Api(tags = "消息黑名单管理")
@RestController
@RequestMapping("/blacklist")
public class NoticeBlacklistController {

    @Autowired
    private NoticeBlacklistService blacklistService;

    @Autowired
    private NoticeContextUtil contextUtil;

    @ApiOperation("分页查询黑名单列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<NoticeBlacklistVO>> pageList(@RequestBody NoticeBlacklistQueryRequest request) {
        return ResponseUtil.ok(blacklistService.pageList(request));
    }

    @ApiOperation("查询黑名单详情")
    @PostMapping("/detail")
    public BaseResponse<NoticeBlacklistVO> detail(@RequestBody NoticeBlacklistVO request) {
        NoticeBlacklistVO vo = blacklistService.detail(request.getId());
        return ResponseUtil.ok(vo);
    }

    @ApiOperation("创建黑名单")
    @PostMapping("/create")
    public BaseResponse<Integer> create(@RequestBody NoticeBlacklistCreateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        int result = blacklistService.create(request, userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("更新黑名单")
    @PostMapping("/update")
    public BaseResponse<Integer> update(@RequestBody NoticeBlacklistUpdateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        int result = blacklistService.update(request, userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("删除黑名单")
    @PostMapping("/delete")
    public BaseResponse<Integer> delete(@RequestBody NoticeBlacklistVO request) {
        int result = blacklistService.delete(request.getId());
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量删除黑名单")
    @PostMapping("/batchDelete")
    public BaseResponse<Integer> batchDelete(@RequestBody List<String> ids) {
        int result = blacklistService.batchDelete(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量启用黑名单")
    @PostMapping("/batchEnable")
    public BaseResponse<Integer> batchEnable(@RequestBody List<String> ids) {
        int result = blacklistService.batchEnable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量禁用黑名单")
    @PostMapping("/batchDisable")
    public BaseResponse<Integer> batchDisable(@RequestBody List<String> ids) {
        int result = blacklistService.batchDisable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("导出黑名单")
    @PostMapping("/export")
    public void export(@RequestBody NoticeBlacklistQueryRequest request, HttpServletResponse response) throws Exception {
        blacklistService.export(request, response);
    }

    @ApiOperation("导入黑名单")
    @PostMapping("/import")
    public BaseResponse<Integer> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        String userId = contextUtil.getCurrentUserId();
        int count = blacklistService.importExcel(file.getInputStream(), userId);
        return ResponseUtil.ok(count);
    }
}
