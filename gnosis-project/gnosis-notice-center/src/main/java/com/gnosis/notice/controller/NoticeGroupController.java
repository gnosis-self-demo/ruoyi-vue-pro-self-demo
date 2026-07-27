package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.config.NoticeContextUtil;
import com.gnosis.notice.dto.group.NoticeGroupCreateRequest;
import com.gnosis.notice.dto.group.NoticeGroupQueryRequest;
import com.gnosis.notice.dto.group.NoticeGroupUpdateRequest;
import com.gnosis.notice.dto.group.NoticeGroupVO;
import com.gnosis.notice.service.NoticeGroupService;
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
 * 消息分组管理接口
 */
@Api(tags = "消息分组管理")
@RestController
@RequestMapping("/group")
public class NoticeGroupController {

    @Autowired
    private NoticeGroupService groupService;

    @Autowired
    private NoticeContextUtil contextUtil;

    @ApiOperation("分页查询分组列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<NoticeGroupVO>> pageList(@RequestBody NoticeGroupQueryRequest request) {
        return ResponseUtil.ok(groupService.pageList(request));
    }

    @ApiOperation("查询分组详情")
    @PostMapping("/detail")
    public BaseResponse<NoticeGroupVO> detail(@RequestBody NoticeGroupVO request) {
        NoticeGroupVO vo = groupService.detail(request.getId());
        return ResponseUtil.ok(vo);
    }

    @ApiOperation("创建分组")
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody NoticeGroupCreateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        String id = groupService.create(request, userId);
        return ResponseUtil.ok(id);
    }

    @ApiOperation("更新分组")
    @PostMapping("/update")
    public BaseResponse<Integer> update(@RequestBody NoticeGroupUpdateRequest request) {
        String userId = contextUtil.getCurrentUserId();
        int result = groupService.update(request, userId);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("删除分组")
    @PostMapping("/delete")
    public BaseResponse<Integer> delete(@RequestBody NoticeGroupVO request) {
        int result = groupService.delete(request.getId());
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量删除分组")
    @PostMapping("/batchDelete")
    public BaseResponse<Integer> batchDelete(@RequestBody List<String> ids) {
        int result = groupService.batchDelete(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量启用分组")
    @PostMapping("/batchEnable")
    public BaseResponse<Integer> batchEnable(@RequestBody List<String> ids) {
        int result = groupService.batchEnable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("批量禁用分组")
    @PostMapping("/batchDisable")
    public BaseResponse<Integer> batchDisable(@RequestBody List<String> ids) {
        int result = groupService.batchDisable(ids);
        return ResponseUtil.ok(result);
    }

    @ApiOperation("导出分组")
    @PostMapping("/export")
    public void export(@RequestBody NoticeGroupQueryRequest request, HttpServletResponse response) throws Exception {
        groupService.export(request, response);
    }

    @ApiOperation("导入分组")
    @PostMapping("/import")
    public BaseResponse<Integer> importExcel(@RequestParam("file") MultipartFile file) throws Exception {
        String userId = contextUtil.getCurrentUserId();
        int count = groupService.importExcel(file.getInputStream(), userId);
        return ResponseUtil.ok(count);
    }
}
