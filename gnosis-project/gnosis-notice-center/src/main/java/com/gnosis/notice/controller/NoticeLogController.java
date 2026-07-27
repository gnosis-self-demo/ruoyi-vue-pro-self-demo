package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.config.NoticeContextUtil;
import com.gnosis.notice.dto.log.NoticeLogQueryRequest;
import com.gnosis.notice.dto.log.NoticeLogVO;
import com.gnosis.notice.service.NoticeLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 消息日志管理接口 - 修复版: userId从上下文获取 + 导入导出
 */
@Api(tags = "消息日志管理")
@RestController
@RequestMapping("/log")
public class NoticeLogController {

    @Autowired
    private NoticeLogService logService;

    @Autowired
    private NoticeContextUtil contextUtil;

    @ApiOperation("分页查询日志列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<NoticeLogVO>> pageList(@RequestBody NoticeLogQueryRequest request) {
        return ResponseUtil.ok(logService.pageList(request));
    }

    @ApiOperation("查询日志详情")
    @PostMapping("/detail")
    public BaseResponse<NoticeLogVO> detail(@RequestBody NoticeLogVO request) {
        NoticeLogVO vo = logService.detail(request.getId());
        return ResponseUtil.ok(vo);
    }

    @ApiOperation("重试发送")
    @PostMapping("/retry")
    public BaseResponse<Integer> retry(@RequestBody NoticeLogVO request) {
        int result = logService.retry(request.getId());
        return ResponseUtil.ok(result);
    }

    @ApiOperation("导出日志")
    @PostMapping("/export")
    public void export(@RequestBody NoticeLogQueryRequest request, HttpServletResponse response) throws Exception {
        logService.export(request, response);
    }

    @ApiOperation("导入日志")
    @PostMapping("/import")
    public BaseResponse<Integer> importExcel(@org.springframework.web.bind.annotation.RequestParam("file") MultipartFile file) throws Exception {
        String userId = contextUtil.getCurrentUserId();
        int count = logService.importExcel(file.getInputStream(), userId);
        return ResponseUtil.ok(count);
    }
}
