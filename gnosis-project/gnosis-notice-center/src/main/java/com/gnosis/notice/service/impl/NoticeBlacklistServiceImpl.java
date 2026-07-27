package com.gnosis.notice.service.impl;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeBlacklist;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistCreateRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistQueryRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistUpdateRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistVO;
import com.gnosis.notice.mapper.NoticeBlacklistMapper;
import com.gnosis.notice.service.NoticeBlacklistService;
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
 * 消息黑名单服务实现
 */
@Service
public class NoticeBlacklistServiceImpl implements NoticeBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(NoticeBlacklistServiceImpl.class);

    @Autowired
    private NoticeBlacklistMapper blacklistMapper;

    @Override
    public PageResult<NoticeBlacklistVO> pageList(NoticeBlacklistQueryRequest request) {
        if (request == null) {
            request = new NoticeBlacklistQueryRequest();
        }
        if (request.getPageNum() == null) {
            request.setPageNum(0);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        Long total = blacklistMapper.countByCondition(request);
        if (total == 0) {
            PageResult<NoticeBlacklistVO> result = new PageResult<NoticeBlacklistVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<NoticeBlacklistVO>());
            return result;
        }

        List<NoticeBlacklist> blacklists = blacklistMapper.selectByCondition(request);
        List<NoticeBlacklistVO> voList = new ArrayList<NoticeBlacklistVO>();
        for (NoticeBlacklist blacklist : blacklists) {
            voList.add(convertToVO(blacklist));
        }

        PageResult<NoticeBlacklistVO> result = new PageResult<NoticeBlacklistVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    @Override
    public NoticeBlacklistVO detail(String id) {
        NoticeBlacklist blacklist = blacklistMapper.selectById(id);
        if (blacklist == null) {
            return null;
        }
        return convertToVO(blacklist);
    }

    @Override
    public int create(NoticeBlacklistCreateRequest request, String userId) {
        NoticeBlacklist blacklist = new NoticeBlacklist();
        blacklist.setId(UUID.randomUUID().toString().replace("-", ""));
        blacklist.setUserId(request.getUserId());
        blacklist.setNoticeType(request.getNoticeType());
        blacklist.setTemplateCode(request.getTemplateCode());
        blacklist.setReason(request.getReason());
        blacklist.setStatus(1);
        blacklist.setCreateUserId(userId);
        blacklist.setUpdateUserId(userId);
        blacklist.setCreateTime(new Date());
        blacklist.setUpdateTime(new Date());

        return blacklistMapper.insert(blacklist);
    }

    @Override
    public int update(NoticeBlacklistUpdateRequest request, String userId) {
        NoticeBlacklist blacklist = blacklistMapper.selectById(request.getId());
        if (blacklist == null) {
            return 0;
        }

        if (request.getUserId() != null) {
            blacklist.setUserId(request.getUserId());
        }
        if (request.getNoticeType() != null) {
            blacklist.setNoticeType(request.getNoticeType());
        }
        if (request.getTemplateCode() != null) {
            blacklist.setTemplateCode(request.getTemplateCode());
        }
        if (request.getReason() != null) {
            blacklist.setReason(request.getReason());
        }
        if (request.getStatus() != null) {
            blacklist.setStatus(request.getStatus());
        }
        blacklist.setUpdateUserId(userId);
        blacklist.setUpdateTime(new Date());

        return blacklistMapper.updateById(blacklist);
    }

    @Override
    public int delete(String id) {
        return blacklistMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return blacklistMapper.deleteByIds(ids);
    }

    @Override
    public int batchEnable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return blacklistMapper.batchUpdateStatus(ids, 1);
    }

    @Override
    public int batchDisable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return blacklistMapper.batchUpdateStatus(ids, 0);
    }

    @Override
    public void export(NoticeBlacklistQueryRequest request, HttpServletResponse response) throws Exception {
        List<NoticeBlacklist> blacklists = blacklistMapper.selectByCondition(request);
        List<String[]> data = new ArrayList<String[]>();
        for (NoticeBlacklist blacklist : blacklists) {
            String[] row = new String[]{
                blacklist.getUserId(),
                blacklist.getNoticeType(),
                blacklist.getTemplateCode() != null ? blacklist.getTemplateCode() : "",
                blacklist.getReason() != null ? blacklist.getReason() : "",
                blacklist.getStatus() != null && blacklist.getStatus() == 1 ? "生效" : "失效",
                blacklist.getCreateUserId() != null ? blacklist.getCreateUserId() : "",
                blacklist.getCreateTime() != null ? blacklist.getCreateTime().toString() : "",
                blacklist.getUpdateUserId() != null ? blacklist.getUpdateUserId() : "",
                blacklist.getUpdateTime() != null ? blacklist.getUpdateTime().toString() : ""
            };
            data.add(row);
        }
        String[] headers = {"用户ID", "通知类型", "模板编码", "退订原因", "状态", "创建人", "创建时间", "更新人", "更新时间"};
        ExcelUtils.export(response, "消息黑名单", "黑名单列表", headers, data);
    }

    @Override
    public int importExcel(InputStream inputStream, String userId) throws Exception {
        List<String[]> data = ExcelUtils.read(inputStream, true);
        int count = 0;
        for (String[] row : data) {
            if (row.length < 2 || StringUtils.isEmpty(row[0])) {
                continue;
            }
            NoticeBlacklist blacklist = new NoticeBlacklist();
            blacklist.setId(UUID.randomUUID().toString().replace("-", ""));
            blacklist.setUserId(row[0]);
            blacklist.setNoticeType(row[1]);
            blacklist.setTemplateCode(row.length > 2 ? row[2] : null);
            blacklist.setReason(row.length > 3 ? row[3] : null);
            blacklist.setStatus(1);
            blacklist.setCreateUserId(userId);
            blacklist.setUpdateUserId(userId);
            blacklist.setCreateTime(new Date());
            blacklist.setUpdateTime(new Date());

            blacklistMapper.insert(blacklist);
            count++;
        }
        return count;
    }

    @Override
    public boolean isBlacklisted(String userId, String noticeType, String templateCode) {
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(noticeType)) {
            return false;
        }
        Long count = blacklistMapper.checkBlacklist(userId, noticeType, templateCode);
        return count != null && count > 0;
    }

    /**
     * 转换为VO
     */
    private NoticeBlacklistVO convertToVO(NoticeBlacklist blacklist) {
        NoticeBlacklistVO vo = new NoticeBlacklistVO();
        vo.setId(blacklist.getId());
        vo.setUserId(blacklist.getUserId());
        vo.setNoticeType(blacklist.getNoticeType());
        vo.setTemplateCode(blacklist.getTemplateCode());
        vo.setReason(blacklist.getReason());
        vo.setStatus(blacklist.getStatus());
        vo.setCreateUserId(blacklist.getCreateUserId());
        vo.setUpdateUserId(blacklist.getUpdateUserId());
        vo.setCreateTime(blacklist.getCreateTime());
        vo.setUpdateTime(blacklist.getUpdateTime());
        return vo;
    }
}
