package com.gnosis.notice.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.common.dto.PageResult;
import com.gnosis.common.util.ResponseUtil;
import com.gnosis.notice.config.NoticeContextUtil;
import com.gnosis.notice.dto.templateversion.NoticeTemplateVersionQueryRequest;
import com.gnosis.notice.dto.templateversion.NoticeTemplateVersionVO;
import com.gnosis.notice.service.NoticeTemplateVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模板版本管理接口
 */
@Api(tags = "模板版本管理")
@RestController
@RequestMapping("/templateVersion")
public class NoticeTemplateVersionController {

    @Autowired
    private NoticeTemplateVersionService templateVersionService;

    @Autowired
    private NoticeContextUtil contextUtil;

    @ApiOperation("分页查询模板版本列表")
    @PostMapping("/page")
    public BaseResponse<PageResult<NoticeTemplateVersionVO>> pageList(@RequestBody NoticeTemplateVersionQueryRequest request) {
        return ResponseUtil.ok(templateVersionService.pageList(request));
    }

    @ApiOperation("查询模板版本详情")
    @PostMapping("/detail")
    public BaseResponse<NoticeTemplateVersionVO> detail(@RequestBody NoticeTemplateVersionVO request) {
        NoticeTemplateVersionVO vo = templateVersionService.detail(request.getId());
        return ResponseUtil.ok(vo);
    }

    @ApiOperation("创建模板版本")
    @PostMapping("/createVersion")
    public BaseResponse<String> createVersion(@RequestBody NoticeTemplateVersionCreateBody body) {
        String userId = contextUtil.getCurrentUserId();
        String id = templateVersionService.createVersion(body.getTemplateId(), body.getChangeRemark(), userId);
        return ResponseUtil.ok(id);
    }

    @ApiOperation("查询模板所有版本")
    @PostMapping("/listByTemplateId")
    public BaseResponse<List<NoticeTemplateVersionVO>> listByTemplateId(@RequestBody NoticeTemplateVersionListBody body) {
        List<NoticeTemplateVersionVO> list = templateVersionService.listByTemplateId(body.getTemplateId());
        return ResponseUtil.ok(list);
    }

    @ApiOperation("删除模板版本")
    @PostMapping("/delete")
    public BaseResponse<Integer> delete(@RequestBody NoticeTemplateVersionVO request) {
        int result = templateVersionService.delete(request.getId());
        return ResponseUtil.ok(result);
    }

    /**
     * 创建版本请求体
     */
    static class NoticeTemplateVersionCreateBody {
        private String templateId;
        private String changeRemark;

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }

        public String getChangeRemark() {
            return changeRemark;
        }

        public void setChangeRemark(String changeRemark) {
            this.changeRemark = changeRemark;
        }
    }

    /**
     * 查询模板版本列表请求体
     */
    static class NoticeTemplateVersionListBody {
        private String templateId;

        public String getTemplateId() {
            return templateId;
        }

        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }
    }
}
