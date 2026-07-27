package com.gnosis.notice.service.impl;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeLog;
import com.gnosis.notice.dto.log.NoticeLogQueryRequest;
import com.gnosis.notice.dto.log.NoticeLogVO;
import com.gnosis.notice.mapper.NoticeLogMapper;
import com.gnosis.notice.service.NoticeLogService;
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

/**
 * 消息日志服务实现 - 修复版: retry_count递增 + 导入导出
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
        NoticeLog logRecord = logMapper.selectById(id);
        if (logRecord == null) {
            return null;
        }
        return convertToVO(logRecord);
    }

    @Override
    public int retry(String id) {
        NoticeLog logRecord = logMapper.selectById(id);
        if (logRecord == null) {
            return 0;
        }
        // 修复: 递增retry_count，而不是仅重置状态
        int newRetryCount = (logRecord.getRetryCount() != null ? logRecord.getRetryCount() : 0) + 1;
        logRecord.setRetryCount(newRetryCount);
        logRecord.setSendStatus(0);
        logRecord.setErrorMsg(null);
        logRecord.setUpdateTime(new Date());
        return logMapper.updateById(logRecord);
    }

    @Override
    public void export(NoticeLogQueryRequest request, HttpServletResponse response) throws Exception {
        if (request == null) {
            request = new NoticeLogQueryRequest();
        }
        request.setPageNum(0);
        request.setPageSize(10000);

        List<NoticeLog> logs = logMapper.selectByCondition(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("发送日志");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "模板编码", "通知类型", "接收人", "主题", "发送状态", "优先级",
                "分组ID", "重试次数", "请求ID", "发送时间", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        String[] statusLabels = {"待发送", "发送中", "成功", "失败"};
        String[] priorityLabels = {"普通", "重要", "紧急"};

        for (int i = 0; i < logs.size(); i++) {
            NoticeLog log = logs.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(log.getId());
            row.createCell(1).setCellValue(log.getTemplateCode());
            row.createCell(2).setCellValue(log.getNoticeType());
            row.createCell(3).setCellValue(log.getReceiver());
            row.createCell(4).setCellValue(log.getSubject());
            row.createCell(5).setCellValue(log.getSendStatus() != null && log.getSendStatus() < statusLabels.length
                    ? statusLabels[log.getSendStatus()] : "");
            row.createCell(6).setCellValue(log.getPriority() != null && log.getPriority() < priorityLabels.length
                    ? priorityLabels[log.getPriority()] : "普通");
            row.createCell(7).setCellValue(log.getGroupId() != null ? log.getGroupId() : "");
            row.createCell(8).setCellValue(log.getRetryCount() != null ? log.getRetryCount().toString() : "0");
            row.createCell(9).setCellValue(log.getRequestId() != null ? log.getRequestId() : "");
            row.createCell(10).setCellValue(log.getSendTime() != null ? log.getSendTime().toString() : "");
            row.createCell(11).setCellValue(log.getCreateTime() != null ? log.getCreateTime().toString() : "");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=notice_log_export.xlsx");
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

            NoticeLog logRecord = new NoticeLog();
            logRecord.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
            logRecord.setTemplateCode(ExcelUtils.getCellStringValue(row.getCell(1)));
            logRecord.setNoticeType(ExcelUtils.getCellStringValue(row.getCell(2)));
            logRecord.setReceiver(ExcelUtils.getCellStringValue(row.getCell(3)));
            logRecord.setSubject(ExcelUtils.getCellStringValue(row.getCell(4)));
            logRecord.setSendStatus(0);
            logRecord.setRetryCount(0);
            logRecord.setMaxRetry(3);
            logRecord.setCreateUserId(userId);
            logRecord.setCreateTime(new Date());
            logRecord.setUpdateTime(new Date());

            logMapper.insert(logRecord);
            count++;
        }
        workbook.close();
        return count;
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
        vo.setPriority(log.getPriority());
        vo.setGroupId(log.getGroupId());
        vo.setSendStatus(log.getSendStatus());
        vo.setErrorMsg(log.getErrorMsg());
        vo.setRetryCount(log.getRetryCount());
        vo.setMaxRetry(log.getMaxRetry());
        vo.setRequestId(log.getRequestId());
        vo.setThirdPartyResponse(log.getThirdPartyResponse());
        vo.setSendTime(log.getSendTime());
        vo.setCreateUserId(log.getCreateUserId());
        vo.setUpdateUserId(log.getUpdateUserId());
        vo.setCreateTime(log.getCreateTime());
        vo.setUpdateTime(log.getUpdateTime());
        return vo;
    }
}
