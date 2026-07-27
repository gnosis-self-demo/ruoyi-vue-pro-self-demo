package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.config.NoticeContextUtil;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendCreateRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendQueryRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendUpdateRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendVO;
import com.gnosis.notice.service.NoticeScheduledSendService;
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
 * 定时发送管理接口
 */
@Api(tags = "定时发送管理")
@RestController
@RequestMapping("/scheduledSend")
public class NoticeScheduledSendController {

    @Autowired
    private NoticeScheduledSendService scheduledSendService;

    @Autowired
    private NoticeContextUtil contextUtil;

    @ApiOperation("分页查询定时发送列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<NoticeScheduledSendVO>> pageList(@RequestBody NoticeScheduledSendQueryRequest request) {
        return ResponseUtil.ok(scheduledSendService.pageList(request));
    }

    @ApiOperation("查询定时发送详情")
    @PostMapping("/detail")
    public BaseResponse<NoticeScheduledSendVO> detail(@RequestBody NoticeScheduledSendVO request) {
        NoticeScheduledSendVO vo = scheduledSendService.detail(request.getId());
        return ResponseUtil.ok(vo);
    }

    @ApiOperation("创建定时发送")
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody NoticeScheduledSendCreateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        String id = scheduledSendService.create(request, userId);
        return ResponseUtil.ok(id);
    }

    @ApiOperation("更新定时发送")
    @PostMapping("/update")
    public BaseResponse<Integer> update(@RequestBody NoticeScheduledSendUpdateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        int result = scheduledSendService.update(request, userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("取消定时发送")
    @PostMapping("/cancel")
    public BaseResponse<Integer> cancel(@RequestBody NoticeScheduledSendVO request) {
        String userId = contextUtil.getCurrentUserId();
        int result = scheduledSendService.cancel(request.getId(), userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("删除定时发送")
    @PostMapping("/delete")
    public BaseResponse<Integer> delete(@RequestBody NoticeScheduledSendVO request) {
        int result = scheduledSendService.delete(request.getId());
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量删除定时发送")
    @PostMapping("/batchDelete")
    public BaseResponse<Integer> batchDelete(@RequestBody List<String> ids) {
        int result = scheduledSendService.batchDelete(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("导出定时发送")
    @PostMapping("/export")
    public void export(@RequestBody NoticeScheduledSendQueryRequest request, HttpServletResponse response) throws Exception {
        scheduledSendService.export(request, response);
    }

    @ApiOperation("导入定时发送")
    @PostMapping("/import")
    public BaseResponse<Integer> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        String userId = contextUtil.getCurrentUserId();
        int count = scheduledSendService.importExcel(file.getInputStream(), userId);
        return ResponseUtil.ok(count);
    }
}
