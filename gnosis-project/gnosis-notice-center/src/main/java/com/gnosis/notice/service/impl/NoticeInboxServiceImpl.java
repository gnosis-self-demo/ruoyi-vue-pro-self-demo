package com.gnosis.notice.service.impl;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeInbox;
import com.gnosis.notice.dto.inbox.NoticeInboxCreateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxQueryRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxVO;
import com.gnosis.notice.mapper.NoticeInboxMapper;
import com.gnosis.notice.service.NoticeInboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 站内信服务实现
 */
@Service
public class NoticeInboxServiceImpl implements NoticeInboxService {

    @Autowired
    private NoticeInboxMapper inboxMapper;

    @Override
    public PageResult<NoticeInboxVO> pageList(NoticeInboxQueryRequest request) {
        if (request == null) {
            request = new NoticeInboxQueryRequest();
        }
        if (request.getPageNum() == null) {
            request.setPageNum(0);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        Long total = inboxMapper.countByCondition(request);
        if (total == 0) {
            PageResult<NoticeInboxVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<NoticeInbox> inboxes = inboxMapper.selectByCondition(request);
        List<NoticeInboxVO> voList = new ArrayList<>();
        for (NoticeInbox inbox : inboxes) {
            voList.add(convertToVO(inbox));
        }

        PageResult<NoticeInboxVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    @Override
    public NoticeInboxVO detail(String id) {
        NoticeInbox inbox = inboxMapper.selectById(id);
        if (inbox == null) {
            return null;
        }
        return convertToVO(inbox);
    }

    @Override
    public Long countUnread(String userId) {
        return inboxMapper.countUnread(userId);
    }

    @Override
    public int create(NoticeInboxCreateRequest request, String userId) {
        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            return 0;
        }

        List<NoticeInbox> list = new ArrayList<>();
        for (String toUserId : request.getUserIds()) {
            NoticeInbox inbox = new NoticeInbox();
            inbox.setId(UUID.randomUUID().toString().replace("-", ""));
            inbox.setUserId(toUserId);
            inbox.setSubject(request.getSubject());
            inbox.setContent(request.getContent());
            inbox.setAttachmentPaths(request.getAttachmentPaths());
            inbox.setIsRead(0);
            inbox.setCreateUserId(userId);
            inbox.setCreateTime(new Date());
            list.add(inbox);
        }

        return inboxMapper.batchInsert(list);
    }

    @Override
    public int markRead(String id) {
        return inboxMapper.markRead(id);
    }

    @Override
    public int batchMarkRead(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return inboxMapper.batchMarkRead(ids);
    }

    @Override
    public int delete(String id) {
        return inboxMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return inboxMapper.deleteByIds(ids);
    }

    private NoticeInboxVO convertToVO(NoticeInbox inbox) {
        NoticeInboxVO vo = new NoticeInboxVO();
        vo.setId(inbox.getId());
        vo.setUserId(inbox.getUserId());
        vo.setSubject(inbox.getSubject());
        vo.setContent(inbox.getContent());
        vo.setAttachmentPaths(inbox.getAttachmentPaths());
        vo.setIsRead(inbox.getIsRead());
        vo.setReadTime(inbox.getReadTime());
        vo.setCreateUserId(inbox.getCreateUserId());
        vo.setCreateTime(inbox.getCreateTime());
        return vo;
    }
}
