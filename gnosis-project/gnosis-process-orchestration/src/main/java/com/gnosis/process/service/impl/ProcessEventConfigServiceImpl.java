package com.gnosis.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.process.domain.ProcessEventConfig;
import com.gnosis.process.dto.ProcessEventConfigCreateRequest;
import com.gnosis.process.dto.ProcessEventConfigQueryRequest;
import com.gnosis.process.dto.ProcessEventConfigUpdateRequest;
import com.gnosis.process.dto.ProcessEventConfigVO;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.mapper.ProcessEventConfigMapper;
import com.gnosis.process.service.ProcessEventConfigService;
import com.gnosis.process.util.IdGenerator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程事件配置 Service 实现类
 */
@Service
public class ProcessEventConfigServiceImpl implements ProcessEventConfigService {

    private static final Logger log = LoggerFactory.getLogger(ProcessEventConfigServiceImpl.class);

    @Autowired
    private ProcessEventConfigMapper processEventConfigMapper;

    @Override
    public ProcessEventConfig getById(String id) {
        log.info("根据ID获取流程事件配置, id={}", id);
        return processEventConfigMapper.selectById(id);
    }

    @Override
    public PageResult<ProcessEventConfigVO> list(ProcessEventConfigQueryRequest request) {
        log.info("分页查询流程事件配置列表, request={}", JSON.toJSONString(request));
        Long total = processEventConfigMapper.selectCount(request);
        List<ProcessEventConfig> records = processEventConfigMapper.selectList(request);
        List<ProcessEventConfigVO> voList = new ArrayList<ProcessEventConfigVO>();
        for (ProcessEventConfig config : records) {
            voList.add(convertToVO(config));
        }
        return new PageResult<ProcessEventConfigVO>(voList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public int save(ProcessEventConfigCreateRequest request) {
        log.info("新增流程事件配置, request={}", JSON.toJSONString(request));
        ProcessEventConfig config = new ProcessEventConfig();
        config.setId(IdGenerator.nextId());
        config.setProcessDefKey(request.getProcessDefKey());
        config.setProcessDefUniqueKey(request.getProcessDefUniqueKey());
        config.setNodeDefKey(request.getNodeDefKey());
        config.setSubmitAction(request.getSubmitAction());
        config.setMatchBusiness(request.getMatchBusiness());
        config.setChangeBusinessStatus(request.getChangeBusinessStatus());
        config.setEventType(request.getEventType());
        config.setEventHandler(request.getEventHandler());
        config.setHandlerConfig(request.getHandlerConfig());
        config.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        String operatorId = request.getCreateUserId() != null ? request.getCreateUserId() : "system";
        config.setCreateUserId(operatorId);
        config.setUpdateUserId(operatorId);
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        return processEventConfigMapper.insert(config);
    }

    @Override
    public int update(ProcessEventConfigUpdateRequest request) {
        log.info("更新流程事件配置, request={}", JSON.toJSONString(request));
        ProcessEventConfig config = new ProcessEventConfig();
        config.setId(request.getId());
        config.setProcessDefKey(request.getProcessDefKey());
        config.setProcessDefUniqueKey(request.getProcessDefUniqueKey());
        config.setNodeDefKey(request.getNodeDefKey());
        config.setSubmitAction(request.getSubmitAction());
        config.setMatchBusiness(request.getMatchBusiness());
        config.setChangeBusinessStatus(request.getChangeBusinessStatus());
        config.setEventType(request.getEventType());
        config.setEventHandler(request.getEventHandler());
        config.setHandlerConfig(request.getHandlerConfig());
        config.setIsActive(request.getIsActive());
        config.setUpdateUserId(request.getUpdateUserId());
        config.setUpdateTime(new Date());
        return processEventConfigMapper.updateById(config);
    }

    @Override
    public int delete(String id) {
        log.info("删除流程事件配置, id={}", id);
        return processEventConfigMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        log.info("批量删除流程事件配置, ids={}", JSON.toJSONString(ids));
        return processEventConfigMapper.deleteByIds(ids);
    }

    @Override
    public int batchEnable(List<String> ids, String updateUserId) {
        log.info("批量启用流程事件配置, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return processEventConfigMapper.batchUpdateActive(ids, true, updateUserId);
    }

    @Override
    public int batchDisable(List<String> ids, String updateUserId) {
        log.info("批量禁用流程事件配置, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return processEventConfigMapper.batchUpdateActive(ids, false, updateUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(MultipartFile file, String operatorId) {
        log.info("导入流程事件配置数据, operatorId={}", operatorId);
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = file.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                ProcessEventConfig config = new ProcessEventConfig();
                config.setId(IdGenerator.nextId());
                config.setProcessDefKey(getCellStringValue(row, 0));
                config.setProcessDefUniqueKey(getCellStringValue(row, 1));
                config.setNodeDefKey(getCellStringValue(row, 2));
                config.setSubmitAction(getCellStringValue(row, 3));
                config.setMatchBusiness(getCellStringValue(row, 4));
                config.setChangeBusinessStatus(getCellStringValue(row, 5));
                config.setEventType(getCellStringValue(row, 6));
                config.setEventHandler(getCellStringValue(row, 7));
                config.setHandlerConfig(getCellStringValue(row, 8));
                String isActiveStr = getCellStringValue(row, 9);
                if (isActiveStr != null && !isActiveStr.isEmpty()) {
                    config.setIsActive(Boolean.valueOf(isActiveStr));
                }
                config.setCreateUserId(operatorId);
                config.setUpdateUserId(operatorId);
                Date now = new Date();
                config.setCreateTime(now);
                config.setUpdateTime(now);
                processEventConfigMapper.insert(config);
            }
            log.info("导入流程事件配置数据完成, 共导入{}条", lastRowNum);
        } catch (IOException e) {
            log.error("导入流程事件配置数据异常", e);
            throw new RuntimeException("导入流程事件配置数据异常", e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    log.error("关闭workbook异常", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭inputStream异常", e);
                }
            }
        }
    }

    @Override
    public void exportData(ProcessEventConfigQueryRequest request, HttpServletResponse response) {
        log.info("导出流程事件配置数据, request={}", JSON.toJSONString(request));
        List<ProcessEventConfig> dataList = processEventConfigMapper.selectAllForExport(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("流程事件配置");

        // 写表头
        String[] headers = {"流程定义键", "流程定义唯一键", "节点定义键", "提交动作", "匹配业务",
                "修改业务状态", "事件类型", "事件处理器", "处理器配置", "是否激活",
                "创建人ID", "更新人ID", "创建时间", "更新时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 写数据行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < dataList.size(); i++) {
            ProcessEventConfig config = dataList.get(i);
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(config.getProcessDefKey() != null ? config.getProcessDefKey() : "");
            dataRow.createCell(1).setCellValue(config.getProcessDefUniqueKey() != null ? config.getProcessDefUniqueKey() : "");
            dataRow.createCell(2).setCellValue(config.getNodeDefKey() != null ? config.getNodeDefKey() : "");
            dataRow.createCell(3).setCellValue(config.getSubmitAction() != null ? config.getSubmitAction() : "");
            dataRow.createCell(4).setCellValue(config.getMatchBusiness() != null ? config.getMatchBusiness() : "");
            dataRow.createCell(5).setCellValue(config.getChangeBusinessStatus() != null ? config.getChangeBusinessStatus() : "");
            dataRow.createCell(6).setCellValue(config.getEventType() != null ? config.getEventType() : "");
            dataRow.createCell(7).setCellValue(config.getEventHandler() != null ? config.getEventHandler() : "");
            dataRow.createCell(8).setCellValue(config.getHandlerConfig() != null ? config.getHandlerConfig() : "");
            dataRow.createCell(9).setCellValue(config.getIsActive() != null ? config.getIsActive().toString() : "");
            dataRow.createCell(10).setCellValue(config.getCreateUserId() != null ? config.getCreateUserId() : "");
            dataRow.createCell(11).setCellValue(config.getUpdateUserId() != null ? config.getUpdateUserId() : "");
            dataRow.createCell(12).setCellValue(config.getCreateTime() != null ? sdf.format(config.getCreateTime()) : "");
            dataRow.createCell(13).setCellValue(config.getUpdateTime() != null ? sdf.format(config.getUpdateTime()) : "");
        }

        // 写入响应
        OutputStream outputStream = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("流程事件配置.xlsx", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            log.info("导出流程事件配置数据完成, 共导出{}条", dataList.size());
        } catch (IOException e) {
            log.error("导出流程事件配置数据异常", e);
            throw new RuntimeException("导出流程事件配置数据异常", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("关闭outputStream异常", e);
                }
            }
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    log.error("关闭workbook异常", e);
                }
            }
        }
    }

    /**
     * 将ProcessEventConfig转换为ProcessEventConfigVO
     *
     * @param config 流程事件配置实体
     * @return 流程事件配置VO
     */
    private ProcessEventConfigVO convertToVO(ProcessEventConfig config) {
        if (config == null) {
            return null;
        }
        ProcessEventConfigVO vo = new ProcessEventConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }

    /**
     * 获取单元格字符串值
     *
     * @param row         行
     * @param columnIndex 列索引
     * @return 字符串值
     */
    private String getCellStringValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
