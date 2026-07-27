package com.gnosis.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeScheduledSend;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendCreateRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendQueryRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendUpdateRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendVO;
import com.gnosis.notice.mapper.NoticeScheduledSendMapper;
import com.gnosis.notice.service.NoticeScheduledSendService;
import com.gnosis.notice.service.NoticeSendService;
import com.gnosis.notice.util.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 定时发送服务实现
 */
@Service
public class NoticeScheduledSendServiceImpl implements NoticeScheduledSendService {

    private static final Logger log = LoggerFactory.getLogger(NoticeScheduledSendServiceImpl.class);

    @Autowired
    private NoticeScheduledSendMapper scheduledSendMapper;

    @Autowired
    private NoticeSendService sendService;

    @Override
    public PageResult<NoticeScheduledSendVO> pageList(NoticeScheduledSendQueryRequest request) {
        if (request == null) {
            request = new NoticeScheduledSendQueryRequest();
        }
        if (request.getPageNum() == null) {
            request.setPageNum(0);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        Long total = scheduledSendMapper.countByCondition(request);
        if (total == 0) {
            PageResult<NoticeScheduledSendVO> result = new PageResult<NoticeScheduledSendVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<NoticeScheduledSendVO>());
            return result;
        }

        List<NoticeScheduledSend> list = scheduledSendMapper.selectByCondition(request);
        List<NoticeScheduledSendVO> voList = new ArrayList<NoticeScheduledSendVO>();
        for (NoticeScheduledSend item : list) {
            voList.add(convertToVO(item));
        }

        PageResult<NoticeScheduledSendVO> result = new PageResult<NoticeScheduledSendVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    @Override
    public NoticeScheduledSendVO detail(String id) {
        NoticeScheduledSend item = scheduledSendMapper.selectById(id);
        if (item == null) {
            return null;
        }
        return convertToVO(item);
    }

    @Override
    public String create(NoticeScheduledSendCreateRequest request, String userId) {
        NoticeScheduledSend scheduledSend = new NoticeScheduledSend();
        scheduledSend.setId(UUID.randomUUID().toString().replace("-", ""));
        scheduledSend.setTemplateCode(request.getTemplateCode());
        scheduledSend.setNoticeType(request.getNoticeType());
        scheduledSend.setReceiver(request.getReceiver());
        scheduledSend.setSubject(request.getSubject());
        scheduledSend.setContent(request.getContent());

        // 如果params不为空，JSON序列化存入
        if (request.getParams() != null && !request.getParams().isEmpty()) {
            scheduledSend.setParams(JSON.toJSONString(request.getParams()));
        }

        // 附件路径序列化
        if (request.getAttachmentPaths() != null && !request.getAttachmentPaths().isEmpty()) {
            scheduledSend.setAttachmentPaths(JSON.toJSONString(request.getAttachmentPaths()));
        }

        scheduledSend.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        scheduledSend.setGroupId(request.getGroupId());
        scheduledSend.setScheduledTime(request.getScheduledTime());
        scheduledSend.setSendStatus(0);
        scheduledSend.setCreateUserId(userId);
        scheduledSend.setUpdateUserId(userId);
        scheduledSend.setCreateTime(new Date());
        scheduledSend.setUpdateTime(new Date());

        scheduledSendMapper.insert(scheduledSend);
        return scheduledSend.getId();
    }

    @Override
    public int update(NoticeScheduledSendUpdateRequest request, String userId) {
        NoticeScheduledSend scheduledSend = scheduledSendMapper.selectById(request.getId());
        if (scheduledSend == null) {
            return 0;
        }

        if (request.getScheduledTime() != null) {
            scheduledSend.setScheduledTime(request.getScheduledTime());
        }
        if (request.getSendStatus() != null) {
            scheduledSend.setSendStatus(request.getSendStatus());
        }
        if (request.getSubject() != null) {
            scheduledSend.setSubject(request.getSubject());
        }
        if (request.getContent() != null) {
            scheduledSend.setContent(request.getContent());
        }
        scheduledSend.setUpdateUserId(userId);
        scheduledSend.setUpdateTime(new Date());

        return scheduledSendMapper.updateById(scheduledSend);
    }

    @Override
    public int cancel(String id, String userId) {
        NoticeScheduledSend scheduledSend = scheduledSendMapper.selectById(id);
        if (scheduledSend == null) {
            return 0;
        }
        scheduledSend.setSendStatus(3);
        scheduledSend.setUpdateUserId(userId);
        scheduledSend.setUpdateTime(new Date());
        return scheduledSendMapper.updateById(scheduledSend);
    }

    @Override
    public int delete(String id) {
        return scheduledSendMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return scheduledSendMapper.deleteByIds(ids);
    }

    @Override
    public void export(NoticeScheduledSendQueryRequest request, HttpServletResponse response) throws Exception {
        List<NoticeScheduledSend> list = scheduledSendMapper.selectByCondition(request);
        List<String[]> data = new ArrayList<String[]>();
        for (NoticeScheduledSend item : list) {
            String sendStatusStr;
            if (item.getSendStatus() == null) {
                sendStatusStr = "--";
            } else {
                switch (item.getSendStatus()) {
                    case 0: sendStatusStr = "待发送"; break;
                    case 1: sendStatusStr = "已发送"; break;
                    case 2: sendStatusStr = "失败"; break;
                    case 3: sendStatusStr = "已取消"; break;
                    default: sendStatusStr = "未知"; break;
                }
            }
            String[] row = new String[]{
                item.getTemplateCode() != null ? item.getTemplateCode() : "",
                item.getNoticeType() != null ? item.getNoticeType() : "",
                item.getReceiver() != null ? item.getReceiver() : "",
                item.getSubject() != null ? item.getSubject() : "",
                item.getContent() != null ? item.getContent() : "",
                sendStatusStr,
                item.getScheduledTime() != null ? item.getScheduledTime().toString() : "",
                item.getCreateUserId() != null ? item.getCreateUserId() : "",
                item.getCreateTime() != null ? item.getCreateTime().toString() : ""
            };
            data.add(row);
        }
        String[] headers = {"模板编码", "通知类型", "接收人", "消息主题", "消息内容", "发送状态",
            "计划发送时间", "创建人", "创建时间"};
        ExcelUtils.export(response, "定时发送", "定时发送列表", headers, data);
    }

    @Override
    public int importExcel(InputStream inputStream, String userId) throws Exception {
        List<String[]> data = ExcelUtils.read(inputStream, true);
        int count = 0;
        for (String[] row : data) {
            if (row.length < 3 || StringUtils.isEmpty(row[0])) {
                continue;
            }
            NoticeScheduledSend scheduledSend = new NoticeScheduledSend();
            scheduledSend.setId(UUID.randomUUID().toString().replace("-", ""));
            scheduledSend.setTemplateCode(row[0]);
            scheduledSend.setNoticeType(row[1]);
            scheduledSend.setReceiver(row[2]);
            scheduledSend.setSubject(row.length > 3 ? row[3] : null);
            scheduledSend.setContent(row.length > 4 ? row[4] : null);
            scheduledSend.setSendStatus(0);
            scheduledSend.setPriority(0);
            scheduledSend.setCreateUserId(userId);
            scheduledSend.setUpdateUserId(userId);
            scheduledSend.setCreateTime(new Date());
            scheduledSend.setUpdateTime(new Date());

            scheduledSendMapper.insert(scheduledSend);
            count++;
        }
        return count;
    }

    /**
     * 转换为VO
     */
    private NoticeScheduledSendVO convertToVO(NoticeScheduledSend item) {
        NoticeScheduledSendVO vo = new NoticeScheduledSendVO();
        vo.setId(item.getId());
        vo.setTemplateCode(item.getTemplateCode());
        vo.setNoticeType(item.getNoticeType());
        vo.setReceiver(item.getReceiver());
        vo.setSubject(item.getSubject());
        vo.setContent(item.getContent());
        vo.setParams(item.getParams());
        vo.setAttachmentPaths(item.getAttachmentPaths());
        vo.setPriority(item.getPriority());
        vo.setGroupId(item.getGroupId());
        vo.setScheduledTime(item.getScheduledTime());
        vo.setSendStatus(item.getSendStatus());
        vo.setLogId(item.getLogId());
        vo.setErrorMsg(item.getErrorMsg());
        vo.setCreateUserId(item.getCreateUserId());
        vo.setUpdateUserId(item.getUpdateUserId());
        vo.setCreateTime(item.getCreateTime());
        vo.setUpdateTime(item.getUpdateTime());
        return vo;
    }
}
