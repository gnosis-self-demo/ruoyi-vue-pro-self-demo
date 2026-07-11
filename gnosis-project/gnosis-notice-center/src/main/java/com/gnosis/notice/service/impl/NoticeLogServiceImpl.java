package com.gnosis.notice.service.impl;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeLog;
import com.gnosis.notice.dto.log.NoticeLogQueryRequest;
import com.gnosis.notice.dto.log.NoticeLogVO;
import com.gnosis.notice.mapper.NoticeLogMapper;
import com.gnosis.notice.service.NoticeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息日志服务实现
 */
@Service
public class NoticeLogServiceImpl implements NoticeLogService {

    @Autowired
    private NoticeLogMapper logMapper;

    @Override
    public PageResult<NoticeLogVO> pageList(NoticeLogQueryRequest request) {
        if (request == null) {
            request = new NoticeLogQueryRequest();
        }
        if (request.getPageNum() == null) {
            request.setPageNum(0);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        Long total = logMapper.countByCondition(request);
        if (total == 0) {
            PageResult<NoticeLogVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<NoticeLog> logs = logMapper.selectByCondition(request);
        List<NoticeLogVO> voList = new ArrayList<>();
        for (NoticeLog log : logs) {
            voList.add(convertToVO(log));
        }

        PageResult<NoticeLogVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    @Override
    public NoticeLogVO detail(String id) {
        NoticeLog log = logMapper.selectById(id);
        if (log == null) {
            return null;
        }
        return convertToVO(log);
    }

    @Override
    public int retry(String id) {
        NoticeLog log = logMapper.selectById(id);
        if (log == null) {
            return 0;
        }
        // 重置状态为待发送
        log.setSendStatus(0);
        return logMapper.updateById(log);
    }

    private NoticeLogVO convertToVO(NoticeLog log) {
        NoticeLogVO vo = new NoticeLogVO();
        vo.setId(log.getId());
        vo.setTemplateId(log.getTemplateId());
        vo.setTemplateCode(log.getTemplateCode());
        vo.setNoticeType(log.getNoticeType());
        vo.setReceiver(log.getReceiver());
        vo.setSubject(log.getSubject());
        vo.setContent(log.getContent());
        vo.setAttachmentPaths(log.getAttachmentPaths());
        vo.setSendStatus(log.getSendStatus());
        vo.setErrorMsg(log.getErrorMsg());
        vo.setRetryCount(log.getRetryCount());
        vo.setMaxRetry(log.getMaxRetry());
        vo.setRequestId(log.getRequestId());
        vo.setSendTime(log.getSendTime());
        vo.setCreateUserId(log.getCreateUserId());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}
