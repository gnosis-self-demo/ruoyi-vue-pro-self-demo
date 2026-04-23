package com.gnosis.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.process.domain.BusinessConfig;
import com.gnosis.process.dto.BusinessConfigCreateRequest;
import com.gnosis.process.dto.BusinessConfigQueryRequest;
import com.gnosis.process.dto.BusinessConfigUpdateRequest;
import com.gnosis.process.dto.BusinessConfigVO;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.mapper.BusinessConfigMapper;
import com.gnosis.process.service.BusinessConfigService;
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
 * 业务配置 Service 实现类
 */
@Service
public class BusinessConfigServiceImpl implements BusinessConfigService {

    private static final Logger log = LoggerFactory.getLogger(BusinessConfigServiceImpl.class);

    @Autowired
    private BusinessConfigMapper businessConfigMapper;

    @Override
    public BusinessConfig getById(String id) {
        log.info("根据ID获取业务配置, id={}", id);
        return businessConfigMapper.selectById(id);
    }

    @Override
    public BusinessConfig getByCode(String code) {
        log.info("根据编码获取业务配置, code={}", code);
        return businessConfigMapper.selectByCode(code);
    }

    @Override
    public PageResult<BusinessConfigVO> list(BusinessConfigQueryRequest request) {
        log.info("分页查询业务配置列表, request={}", JSON.toJSONString(request));
        Long total = businessConfigMapper.selectCount(request);
        List<BusinessConfig> records = businessConfigMapper.selectList(request);
        List<BusinessConfigVO> voList = new ArrayList<BusinessConfigVO>();
        for (BusinessConfig config : records) {
            voList.add(convertToVO(config));
        }
        return new PageResult<BusinessConfigVO>(voList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public int save(BusinessConfigCreateRequest request) {
        log.info("新增业务配置, request={}", JSON.toJSONString(request));
        BusinessConfig config = new BusinessConfig();
        config.setId(IdGenerator.nextId());
        config.setName(request.getName());
        config.setCode(request.getCode());
        config.setDescription(request.getDescription());
        config.setBusinessType(request.getBusinessType() != null ? request.getBusinessType() : "default");
        config.setBusinessCategory(request.getBusinessCategory());
        config.setRuleEngineConfigId(request.getRuleEngineConfigId());
        config.setRuleEngineType(request.getRuleEngineType());
        config.setWorkflowEnabled(request.getWorkflowEnabled() != null ? request.getWorkflowEnabled() : true);
        config.setStatus(request.getStatus() != null ? request.getStatus() : "active");
        config.setTenantId(request.getTenantId());
        String operatorId = request.getCreateUserId() != null ? request.getCreateUserId() : "system";
        config.setCreateUserId(operatorId);
        config.setUpdateUserId(operatorId);
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        return businessConfigMapper.insert(config);
    }

    @Override
    public int update(BusinessConfigUpdateRequest request) {
        log.info("更新业务配置, request={}", JSON.toJSONString(request));
        BusinessConfig config = new BusinessConfig();
        config.setId(request.getId());
        config.setName(request.getName());
        config.setCode(request.getCode());
        config.setDescription(request.getDescription());
        config.setBusinessType(request.getBusinessType());
        config.setBusinessCategory(request.getBusinessCategory());
        config.setRuleEngineConfigId(request.getRuleEngineConfigId());
        config.setRuleEngineType(request.getRuleEngineType());
        config.setWorkflowEnabled(request.getWorkflowEnabled());
        config.setStatus(request.getStatus());
        config.setTenantId(request.getTenantId());
        config.setUpdateUserId(request.getUpdateUserId());
        config.setUpdateTime(new Date());
        return businessConfigMapper.updateById(config);
    }

    @Override
    public int delete(String id) {
        log.info("删除业务配置, id={}", id);
        return businessConfigMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        log.info("批量删除业务配置, ids={}", JSON.toJSONString(ids));
        return businessConfigMapper.deleteByIds(ids);
    }

    @Override
    public int batchEnable(List<String> ids, String updateUserId) {
        log.info("批量启用业务配置, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return businessConfigMapper.batchUpdateStatus(ids, "active", updateUserId);
    }

    @Override
    public int batchDisable(List<String> ids, String updateUserId) {
        log.info("批量禁用业务配置, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return businessConfigMapper.batchUpdateStatus(ids, "locked", updateUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(MultipartFile file, String operatorId) {
        log.info("导入业务配置数据, operatorId={}", operatorId);
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
                BusinessConfig config = new BusinessConfig();
                config.setId(IdGenerator.nextId());
                config.setName(getCellStringValue(row, 0));
                config.setCode(getCellStringValue(row, 1));
                config.setDescription(getCellStringValue(row, 2));
                config.setBusinessType(getCellStringValue(row, 3));
                config.setBusinessCategory(getCellStringValue(row, 4));
                config.setRuleEngineConfigId(getCellStringValue(row, 5));
                config.setRuleEngineType(getCellStringValue(row, 6));
                String workflowEnabledStr = getCellStringValue(row, 7);
                if (workflowEnabledStr != null && !workflowEnabledStr.isEmpty()) {
                    config.setWorkflowEnabled(Boolean.valueOf(workflowEnabledStr));
                }
                config.setStatus(getCellStringValue(row, 8));
                config.setTenantId(getCellStringValue(row, 9));
                config.setCreateUserId(operatorId);
                config.setUpdateUserId(operatorId);
                Date now = new Date();
                config.setCreateTime(now);
                config.setUpdateTime(now);
                businessConfigMapper.insert(config);
            }
            log.info("导入业务配置数据完成, 共导入{}条", lastRowNum);
        } catch (IOException e) {
            log.error("导入业务配置数据异常", e);
            throw new RuntimeException("导入业务配置数据异常", e);
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
    public void exportData(BusinessConfigQueryRequest request, HttpServletResponse response) {
        log.info("导出业务配置数据, request={}", JSON.toJSONString(request));
        List<BusinessConfig> dataList = businessConfigMapper.selectAllForExport(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("业务配置");

        // 写表头
        String[] headers = {"业务名称", "业务编码", "业务描述", "业务类型", "业务分类", "规则引擎配置ID",
                "规则引擎类型", "是否启用工作流", "状态", "租户ID", "创建人ID", "更新人ID", "创建时间", "更新时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 写数据行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < dataList.size(); i++) {
            BusinessConfig config = dataList.get(i);
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(config.getName() != null ? config.getName() : "");
            dataRow.createCell(1).setCellValue(config.getCode() != null ? config.getCode() : "");
            dataRow.createCell(2).setCellValue(config.getDescription() != null ? config.getDescription() : "");
            dataRow.createCell(3).setCellValue(config.getBusinessType() != null ? config.getBusinessType() : "");
            dataRow.createCell(4).setCellValue(config.getBusinessCategory() != null ? config.getBusinessCategory() : "");
            dataRow.createCell(5).setCellValue(config.getRuleEngineConfigId() != null ? config.getRuleEngineConfigId() : "");
            dataRow.createCell(6).setCellValue(config.getRuleEngineType() != null ? config.getRuleEngineType() : "");
            dataRow.createCell(7).setCellValue(config.getWorkflowEnabled() != null ? config.getWorkflowEnabled().toString() : "");
            dataRow.createCell(8).setCellValue(config.getStatus() != null ? config.getStatus() : "");
            dataRow.createCell(9).setCellValue(config.getTenantId() != null ? config.getTenantId() : "");
            dataRow.createCell(10).setCellValue(config.getCreateUserId() != null ? config.getCreateUserId() : "");
            dataRow.createCell(11).setCellValue(config.getUpdateUserId() != null ? config.getUpdateUserId() : "");
            dataRow.createCell(12).setCellValue(config.getCreateTime() != null ? sdf.format(config.getCreateTime()) : "");
            dataRow.createCell(13).setCellValue(config.getUpdateTime() != null ? sdf.format(config.getUpdateTime()) : "");
        }

        // 写入响应
        OutputStream outputStream = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("业务配置.xlsx", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            log.info("导出业务配置数据完成, 共导出{}条", dataList.size());
        } catch (IOException e) {
            log.error("导出业务配置数据异常", e);
            throw new RuntimeException("导出业务配置数据异常", e);
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
     * 将BusinessConfig转换为BusinessConfigVO
     *
     * @param config 业务配置实体
     * @return 业务配置VO
     */
    private BusinessConfigVO convertToVO(BusinessConfig config) {
        if (config == null) {
            return null;
        }
        BusinessConfigVO vo = new BusinessConfigVO();
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
