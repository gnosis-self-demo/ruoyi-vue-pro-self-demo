package com.gnosis.notice.service.impl;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeInbox;
import com.gnosis.notice.dto.inbox.NoticeInboxCreateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxQueryRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxUpdateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxVO;
import com.gnosis.notice.mapper.NoticeInboxMapper;
import com.gnosis.notice.service.NoticeInboxService;
import com.gnosis.notice.util.ExcelUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 站内信服务实现 - 修复版: 编辑修改/导入导出/批量启用禁用
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
            inbox.setPriority(request.getPriority() != null ? request.getPriority() : 0);
            inbox.setGroupId(request.getGroupId());
            inbox.setIsRead(0);
            inbox.setStatus(1);
            inbox.setCreateUserId(userId);
            inbox.setUpdateTime(new Date());
            inbox.setCreateTime(new Date());
            list.add(inbox);
        }

        return inboxMapper.batchInsert(list);
    }

    @Override
    public int update(NoticeInboxUpdateRequest request, String userId) {
        NoticeInbox inbox = inboxMapper.selectById(request.getId());
        if (inbox == null) {
            return 0;
        }
        if (request.getSubject() != null) {
            inbox.setSubject(request.getSubject());
        }
        if (request.getContent() != null) {
            inbox.setContent(request.getContent());
        }
        if (request.getPriority() != null) {
            inbox.setPriority(request.getPriority());
        }
        if (request.getGroupId() != null) {
            inbox.setGroupId(request.getGroupId());
        }
        inbox.setUpdateUserId(userId);
        inbox.setUpdateTime(new Date());
        return inboxMapper.updateById(inbox);
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

    @Override
    public int batchEnable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return inboxMapper.batchUpdateStatus(ids, 1);
    }

    @Override
    public int batchDisable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return inboxMapper.batchUpdateStatus(ids, 0);
    }

    @Override
    public void export(NoticeInboxQueryRequest request, HttpServletResponse response) throws Exception {
        if (request == null) {
            request = new NoticeInboxQueryRequest();
        }
        request.setPageNum(0);
        request.setPageSize(10000);

        List<NoticeInbox> inboxes = inboxMapper.selectByCondition(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("站内信");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "用户ID", "主题", "内容", "优先级", "分组ID",
                "是否已读", "状态", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        String[] priorityLabels = {"普通", "重要", "紧急"};

        for (int i = 0; i < inboxes.size(); i++) {
            NoticeInbox inbox = inboxes.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(inbox.getId());
            row.createCell(1).setCellValue(inbox.getUserId());
            row.createCell(2).setCellValue(inbox.getSubject());
            row.createCell(3).setCellValue(inbox.getContent());
            row.createCell(4).setCellValue(inbox.getPriority() != null && inbox.getPriority() < priorityLabels.length
                    ? priorityLabels[inbox.getPriority()] : "普通");
            row.createCell(5).setCellValue(inbox.getGroupId() != null ? inbox.getGroupId() : "");
            row.createCell(6).setCellValue(inbox.getIsRead() == 1 ? "已读" : "未读");
            row.createCell(7).setCellValue(inbox.getStatus() == 1 ? "正常" : "归档");
            row.createCell(8).setCellValue(inbox.getCreateTime() != null ? inbox.getCreateTime().toString() : "");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=notice_inbox_export.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Override
    public int importExcel(InputStream inputStream, String userId) throws Exception {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        int count = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            NoticeInbox inbox = new NoticeInbox();
            inbox.setId(UUID.randomUUID().toString().replace("-", ""));
            inbox.setUserId(ExcelUtils.getCellStringValue(row.getCell(1)));
            inbox.setSubject(ExcelUtils.getCellStringValue(row.getCell(2)));
            inbox.setContent(ExcelUtils.getCellStringValue(row.getCell(3)));
            inbox.setIsRead(0);
            inbox.setStatus(1);
            inbox.setCreateUserId(userId);
            inbox.setCreateTime(new Date());
            inbox.setUpdateTime(new Date());

            inboxMapper.insert(inbox);
            count++;
        }
        workbook.close();
        return count;
    }

    private NoticeInboxVO convertToVO(NoticeInbox inbox) {
        NoticeInboxVO vo = new NoticeInboxVO();
        vo.setId(inbox.getId());
        vo.setUserId(inbox.getUserId());
        vo.setSubject(inbox.getSubject());
        vo.setContent(inbox.getContent());
        vo.setAttachmentPaths(inbox.getAttachmentPaths());
        vo.setPriority(inbox.getPriority());
        vo.setGroupId(inbox.getGroupId());
        vo.setIsRead(inbox.getIsRead());
        vo.setReadTime(inbox.getReadTime());
        vo.setStatus(inbox.getStatus());
        vo.setCreateUserId(inbox.getCreateUserId());
        vo.setUpdateUserId(inbox.getUpdateUserId());
        vo.setCreateTime(inbox.getCreateTime());
        vo.setUpdateTime(inbox.getUpdateTime());
        return vo;
    }
}
