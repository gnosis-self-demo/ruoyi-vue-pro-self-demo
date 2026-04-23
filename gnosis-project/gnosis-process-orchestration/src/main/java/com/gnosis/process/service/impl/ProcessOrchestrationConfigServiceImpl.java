package com.gnosis.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.process.domain.ProcessOrchestrationConfig;
import com.gnosis.process.dto.ProcessOrchestrationConfigCreateRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigQueryRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigUpdateRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigVO;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.mapper.ProcessOrchestrationConfigMapper;
import com.gnosis.process.service.ProcessOrchestrationConfigService;
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
 * 流程编排配置 Service 实现类
 */
@Service
public class ProcessOrchestrationConfigServiceImpl implements ProcessOrchestrationConfigService {

    private static final Logger log = LoggerFactory.getLogger(ProcessOrchestrationConfigServiceImpl.class);

    @Autowired
    private ProcessOrchestrationConfigMapper processOrchestrationConfigMapper;

    @Override
    public ProcessOrchestrationConfig getById(String id) {
        log.info("根据ID获取流程编排配置, id={}", id);
        return processOrchestrationConfigMapper.selectById(id);
    }

    @Override
    public PageResult<ProcessOrchestrationConfigVO> list(ProcessOrchestrationConfigQueryRequest request) {
        log.info("分页查询流程编排配置列表, request={}", JSON.toJSONString(request));
        Long total = processOrchestrationConfigMapper.selectCount(request);
        List<ProcessOrchestrationConfig> records = processOrchestrationConfigMapper.selectList(request);
        List<ProcessOrchestrationConfigVO> voList = new ArrayList<ProcessOrchestrationConfigVO>();
        for (ProcessOrchestrationConfig config : records) {
            voList.add(convertToVO(config));
        }
        return new PageResult<ProcessOrchestrationConfigVO>(voList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<ProcessOrchestrationConfigVO> listByBusinessConfigId(String businessConfigId) {
        log.info("根据业务配置ID查询流程编排配置列表, businessConfigId={}", businessConfigId);
        List<ProcessOrchestrationConfig> configs = processOrchestrationConfigMapper.selectByBusinessConfigId(businessConfigId);
        List<ProcessOrchestrationConfigVO> voList = new ArrayList<ProcessOrchestrationConfigVO>();
        for (ProcessOrchestrationConfig config : configs) {
            voList.add(convertToVO(config));
        }
        return voList;
    }

    @Override
    public int save(ProcessOrchestrationConfigCreateRequest request) {
        log.info("新增流程编排配置, request={}", JSON.toJSONString(request));
        ProcessOrchestrationConfig config = new ProcessOrchestrationConfig();
        config.setId(IdGenerator.nextId());
        config.setBusinessConfigId(request.getBusinessConfigId());
        config.setCurrentProcessDefKey(request.getCurrentProcessDefKey());
        config.setCurrentProcessDefUniqueKey(request.getCurrentProcessDefUniqueKey());
        config.setCurrentProcessSystem(request.getCurrentProcessSystem());
        config.setNextProcessDefKey(request.getNextProcessDefKey());
        config.setNextProcessDefUniqueKey(request.getNextProcessDefUniqueKey());
        config.setNextProcessSystem(request.getNextProcessSystem());
        config.setTriggerEvent(request.getTriggerEvent());
        config.setConditionExpression(request.getConditionExpression());
        config.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        config.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        String operatorId = request.getCreateUserId() != null ? request.getCreateUserId() : "system";
        config.setCreateUserId(operatorId);
        config.setUpdateUserId(operatorId);
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        return processOrchestrationConfigMapper.insert(config);
    }

    @Override
    public int update(ProcessOrchestrationConfigUpdateRequest request) {
        log.info("更新流程编排配置, request={}", JSON.toJSONString(request));
        ProcessOrchestrationConfig config = new ProcessOrchestrationConfig();
        config.setId(request.getId());
        config.setBusinessConfigId(request.getBusinessConfigId());
        config.setCurrentProcessDefKey(request.getCurrentProcessDefKey());
        config.setCurrentProcessDefUniqueKey(request.getCurrentProcessDefUniqueKey());
        config.setCurrentProcessSystem(request.getCurrentProcessSystem());
        config.setNextProcessDefKey(request.getNextProcessDefKey());
        config.setNextProcessDefUniqueKey(request.getNextProcessDefUniqueKey());
        config.setNextProcessSystem(request.getNextProcessSystem());
        config.setTriggerEvent(request.getTriggerEvent());
        config.setConditionExpression(request.getConditionExpression());
        config.setPriority(request.getPriority());
        config.setIsActive(request.getIsActive());
        config.setUpdateUserId(request.getUpdateUserId());
        config.setUpdateTime(new Date());
        return processOrchestrationConfigMapper.updateById(config);
    }

    @Override
    public int delete(String id) {
        log.info("删除流程编排配置, id={}", id);
        return processOrchestrationConfigMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        log.info("批量删除流程编排配置, ids={}", JSON.toJSONString(ids));
        return processOrchestrationConfigMapper.deleteByIds(ids);
    }

    @Override
    public int batchEnable(List<String> ids, String updateUserId) {
        log.info("批量启用流程编排配置, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return processOrchestrationConfigMapper.batchUpdateActive(ids, true, updateUserId);
    }

    @Override
    public int batchDisable(List<String> ids, String updateUserId) {
        log.info("批量禁用流程编排配置, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return processOrchestrationConfigMapper.batchUpdateActive(ids, false, updateUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(MultipartFile file, String operatorId) {
        log.info("导入流程编排配置数据, operatorId={}", operatorId);
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
                ProcessOrchestrationConfig config = new ProcessOrchestrationConfig();
                config.setId(IdGenerator.nextId());
                config.setBusinessConfigId(getCellStringValue(row, 0));
                config.setCurrentProcessDefKey(getCellStringValue(row, 1));
                config.setCurrentProcessDefUniqueKey(getCellStringValue(row, 2));
                config.setCurrentProcessSystem(getCellStringValue(row, 3));
                config.setNextProcessDefKey(getCellStringValue(row, 4));
                config.setNextProcessDefUniqueKey(getCellStringValue(row, 5));
                config.setNextProcessSystem(getCellStringValue(row, 6));
                config.setTriggerEvent(getCellStringValue(row, 7));
                config.setConditionExpression(getCellStringValue(row, 8));
                String priorityStr = getCellStringValue(row, 9);
                if (priorityStr != null && !priorityStr.isEmpty()) {
                    config.setPriority(Integer.valueOf(priorityStr));
                }
                String isActiveStr = getCellStringValue(row, 10);
                if (isActiveStr != null && !isActiveStr.isEmpty()) {
                    config.setIsActive(Boolean.valueOf(isActiveStr));
                }
                config.setCreateUserId(operatorId);
                config.setUpdateUserId(operatorId);
                Date now = new Date();
                config.setCreateTime(now);
                config.setUpdateTime(now);
                processOrchestrationConfigMapper.insert(config);
            }
            log.info("导入流程编排配置数据完成, 共导入{}条", lastRowNum);
        } catch (IOException e) {
            log.error("导入流程编排配置数据异常", e);
            throw new RuntimeException("导入流程编排配置数据异常", e);
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
    public void exportData(ProcessOrchestrationConfigQueryRequest request, HttpServletResponse response) {
        log.info("导出流程编排配置数据, request={}", JSON.toJSONString(request));
        List<ProcessOrchestrationConfig> dataList = processOrchestrationConfigMapper.selectAllForExport(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("流程编排配置");

        // 写表头
        String[] headers = {"业务配置ID", "当前流程定义", "当前流程唯一键", "当前流程平台",
                "下一流程定义", "下一流程唯一键", "下一流程平台", "触发事件", "条件表达式",
                "优先级", "是否激活", "创建人ID", "更新人ID", "创建时间", "更新时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 写数据行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < dataList.size(); i++) {
            ProcessOrchestrationConfig config = dataList.get(i);
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(config.getBusinessConfigId() != null ? config.getBusinessConfigId() : "");
            dataRow.createCell(1).setCellValue(config.getCurrentProcessDefKey() != null ? config.getCurrentProcessDefKey() : "");
            dataRow.createCell(2).setCellValue(config.getCurrentProcessDefUniqueKey() != null ? config.getCurrentProcessDefUniqueKey() : "");
            dataRow.createCell(3).setCellValue(config.getCurrentProcessSystem() != null ? config.getCurrentProcessSystem() : "");
            dataRow.createCell(4).setCellValue(config.getNextProcessDefKey() != null ? config.getNextProcessDefKey() : "");
            dataRow.createCell(5).setCellValue(config.getNextProcessDefUniqueKey() != null ? config.getNextProcessDefUniqueKey() : "");
            dataRow.createCell(6).setCellValue(config.getNextProcessSystem() != null ? config.getNextProcessSystem() : "");
            dataRow.createCell(7).setCellValue(config.getTriggerEvent() != null ? config.getTriggerEvent() : "");
            dataRow.createCell(8).setCellValue(config.getConditionExpression() != null ? config.getConditionExpression() : "");
            dataRow.createCell(9).setCellValue(config.getPriority() != null ? config.getPriority().toString() : "");
            dataRow.createCell(10).setCellValue(config.getIsActive() != null ? config.getIsActive().toString() : "");
            dataRow.createCell(11).setCellValue(config.getCreateUserId() != null ? config.getCreateUserId() : "");
            dataRow.createCell(12).setCellValue(config.getUpdateUserId() != null ? config.getUpdateUserId() : "");
            dataRow.createCell(13).setCellValue(config.getCreateTime() != null ? sdf.format(config.getCreateTime()) : "");
            dataRow.createCell(14).setCellValue(config.getUpdateTime() != null ? sdf.format(config.getUpdateTime()) : "");
        }

        // 写入响应
        OutputStream outputStream = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("流程编排配置.xlsx", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            log.info("导出流程编排配置数据完成, 共导出{}条", dataList.size());
        } catch (IOException e) {
            log.error("导出流程编排配置数据异常", e);
            throw new RuntimeException("导出流程编排配置数据异常", e);
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
     * 将ProcessOrchestrationConfig转换为ProcessOrchestrationConfigVO
     *
     * @param config 流程编排配置实体
     * @return 流程编排配置VO
     */
    private ProcessOrchestrationConfigVO convertToVO(ProcessOrchestrationConfig config) {
        if (config == null) {
            return null;
        }
        ProcessOrchestrationConfigVO vo = new ProcessOrchestrationConfigVO();
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
