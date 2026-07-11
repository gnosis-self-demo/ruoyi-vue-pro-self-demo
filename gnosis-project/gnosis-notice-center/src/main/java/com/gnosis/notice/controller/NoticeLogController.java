package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
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

/**
 * 消息日志管理接口
 */
@Api(tags = "消息日志管理")
@RestController
@RequestMapping("/log")
public class NoticeLogController {

    @Autowired
    private NoticeLogService logService;

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
}
