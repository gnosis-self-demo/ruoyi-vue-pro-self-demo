package com.gnosis.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.process.domain.BusinessResourceBinding;
import com.gnosis.process.dto.BusinessResourceBindingCreateRequest;
import com.gnosis.process.dto.BusinessResourceBindingQueryRequest;
import com.gnosis.process.dto.BusinessResourceBindingUpdateRequest;
import com.gnosis.process.dto.BusinessResourceBindingVO;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.mapper.BusinessResourceBindingMapper;
import com.gnosis.process.service.BusinessResourceBindingService;
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
 * 业务资源绑定 Service 实现类
 */
@Service
public class BusinessResourceBindingServiceImpl implements BusinessResourceBindingService {

    private static final Logger log = LoggerFactory.getLogger(BusinessResourceBindingServiceImpl.class);

    @Autowired
    private BusinessResourceBindingMapper businessResourceBindingMapper;

    @Override
    public BusinessResourceBinding getById(String id) {
        log.info("根据ID获取业务资源绑定, id={}", id);
        return businessResourceBindingMapper.selectById(id);
    }

    @Override
    public PageResult<BusinessResourceBindingVO> list(BusinessResourceBindingQueryRequest request) {
        log.info("分页查询业务资源绑定列表, request={}", JSON.toJSONString(request));
        Long total = businessResourceBindingMapper.selectCount(request);
        List<BusinessResourceBinding> records = businessResourceBindingMapper.selectList(request);
        List<BusinessResourceBindingVO> voList = new ArrayList<BusinessResourceBindingVO>();
        for (BusinessResourceBinding binding : records) {
            voList.add(convertToVO(binding));
        }
        return new PageResult<BusinessResourceBindingVO>(voList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<BusinessResourceBindingVO> listByBusinessConfigId(String businessConfigId) {
        log.info("根据业务配置ID查询业务资源绑定列表, businessConfigId={}", businessConfigId);
        List<BusinessResourceBinding> bindings = businessResourceBindingMapper.selectByBusinessConfigId(businessConfigId);
        List<BusinessResourceBindingVO> voList = new ArrayList<BusinessResourceBindingVO>();
        for (BusinessResourceBinding binding : bindings) {
            voList.add(convertToVO(binding));
        }
        return voList;
    }

    @Override
    public List<BusinessResourceBindingVO> listByBusinessCodeAndType(String businessCode, String resourceType) {
        log.info("根据业务编码和资源类型查询业务资源绑定列表, businessCode={}, resourceType={}", businessCode, resourceType);
        List<BusinessResourceBinding> bindings = businessResourceBindingMapper.selectByBusinessCodeAndType(businessCode, resourceType);
        List<BusinessResourceBindingVO> voList = new ArrayList<BusinessResourceBindingVO>();
        for (BusinessResourceBinding binding : bindings) {
            voList.add(convertToVO(binding));
        }
        return voList;
    }

    @Override
    public int save(BusinessResourceBindingCreateRequest request) {
        log.info("新增业务资源绑定, request={}", JSON.toJSONString(request));
        BusinessResourceBinding binding = new BusinessResourceBinding();
        binding.setId(IdGenerator.nextId());
        binding.setBusinessConfigId(request.getBusinessConfigId());
        binding.setResourceType(request.getResourceType());
        binding.setResourceId(request.getResourceId());
        binding.setResourceName(request.getResourceName());
        binding.setBindingType(request.getBindingType());
        binding.setBindingPriority(request.getBindingPriority() != null ? request.getBindingPriority() : 0);
        binding.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        binding.setMetadata(request.getMetadata());
        String operatorId = request.getCreateUserId() != null ? request.getCreateUserId() : "system";
        binding.setCreateUserId(operatorId);
        binding.setUpdateUserId(operatorId);
        Date now = new Date();
        binding.setCreateTime(now);
        binding.setUpdateTime(now);
        return businessResourceBindingMapper.insert(binding);
    }

    @Override
    public int update(BusinessResourceBindingUpdateRequest request) {
        log.info("更新业务资源绑定, request={}", JSON.toJSONString(request));
        BusinessResourceBinding binding = new BusinessResourceBinding();
        binding.setId(request.getId());
        binding.setBusinessConfigId(request.getBusinessConfigId());
        binding.setResourceType(request.getResourceType());
        binding.setResourceId(request.getResourceId());
        binding.setResourceName(request.getResourceName());
        binding.setBindingType(request.getBindingType());
        binding.setBindingPriority(request.getBindingPriority());
        binding.setIsActive(request.getIsActive());
        binding.setMetadata(request.getMetadata());
        binding.setUpdateUserId(request.getUpdateUserId());
        binding.setUpdateTime(new Date());
        return businessResourceBindingMapper.updateById(binding);
    }

    @Override
    public int delete(String id) {
        log.info("删除业务资源绑定, id={}", id);
        return businessResourceBindingMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        log.info("批量删除业务资源绑定, ids={}", JSON.toJSONString(ids));
        return businessResourceBindingMapper.deleteByIds(ids);
    }

    @Override
    public int batchEnable(List<String> ids, String updateUserId) {
        log.info("批量启用业务资源绑定, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return businessResourceBindingMapper.batchUpdateActive(ids, true, updateUserId);
    }

    @Override
    public int batchDisable(List<String> ids, String updateUserId) {
        log.info("批量禁用业务资源绑定, ids={}, updateUserId={}", JSON.toJSONString(ids), updateUserId);
        return businessResourceBindingMapper.batchUpdateActive(ids, false, updateUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importData(MultipartFile file, String operatorId) {
        log.info("导入业务资源绑定数据, operatorId={}", operatorId);
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
                BusinessResourceBinding binding = new BusinessResourceBinding();
                binding.setId(IdGenerator.nextId());
                binding.setBusinessConfigId(getCellStringValue(row, 0));
                binding.setResourceType(getCellStringValue(row, 1));
                binding.setResourceId(getCellStringValue(row, 2));
                binding.setResourceName(getCellStringValue(row, 3));
                binding.setBindingType(getCellStringValue(row, 4));
                String bindingPriorityStr = getCellStringValue(row, 5);
                if (bindingPriorityStr != null && !bindingPriorityStr.isEmpty()) {
                    binding.setBindingPriority(Integer.valueOf(bindingPriorityStr));
                }
                String isActiveStr = getCellStringValue(row, 6);
                if (isActiveStr != null && !isActiveStr.isEmpty()) {
                    binding.setIsActive(Boolean.valueOf(isActiveStr));
                }
                binding.setMetadata(getCellStringValue(row, 7));
                binding.setCreateUserId(operatorId);
                binding.setUpdateUserId(operatorId);
                Date now = new Date();
                binding.setCreateTime(now);
                binding.setUpdateTime(now);
                businessResourceBindingMapper.insert(binding);
            }
            log.info("导入业务资源绑定数据完成, 共导入{}条", lastRowNum);
        } catch (IOException e) {
            log.error("导入业务资源绑定数据异常", e);
            throw new RuntimeException("导入业务资源绑定数据异常", e);
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
    public void exportData(BusinessResourceBindingQueryRequest request, HttpServletResponse response) {
        log.info("导出业务资源绑定数据, request={}", JSON.toJSONString(request));
        List<BusinessResourceBinding> dataList = businessResourceBindingMapper.selectAllForExport(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("业务资源绑定");

        // 写表头
        String[] headers = {"业务配置ID", "资源类型", "资源ID", "资源名称", "绑定类型", "绑定优先级",
                "是否激活", "元数据", "创建人ID", "更新人ID", "创建时间", "更新时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 写数据行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < dataList.size(); i++) {
            BusinessResourceBinding binding = dataList.get(i);
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(binding.getBusinessConfigId() != null ? binding.getBusinessConfigId() : "");
            dataRow.createCell(1).setCellValue(binding.getResourceType() != null ? binding.getResourceType() : "");
            dataRow.createCell(2).setCellValue(binding.getResourceId() != null ? binding.getResourceId() : "");
            dataRow.createCell(3).setCellValue(binding.getResourceName() != null ? binding.getResourceName() : "");
            dataRow.createCell(4).setCellValue(binding.getBindingType() != null ? binding.getBindingType() : "");
            dataRow.createCell(5).setCellValue(binding.getBindingPriority() != null ? binding.getBindingPriority().toString() : "");
            dataRow.createCell(6).setCellValue(binding.getIsActive() != null ? binding.getIsActive().toString() : "");
            dataRow.createCell(7).setCellValue(binding.getMetadata() != null ? binding.getMetadata() : "");
            dataRow.createCell(8).setCellValue(binding.getCreateUserId() != null ? binding.getCreateUserId() : "");
            dataRow.createCell(9).setCellValue(binding.getUpdateUserId() != null ? binding.getUpdateUserId() : "");
            dataRow.createCell(10).setCellValue(binding.getCreateTime() != null ? sdf.format(binding.getCreateTime()) : "");
            dataRow.createCell(11).setCellValue(binding.getUpdateTime() != null ? sdf.format(binding.getUpdateTime()) : "");
        }

        // 写入响应
        OutputStream outputStream = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("业务资源绑定.xlsx", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            log.info("导出业务资源绑定数据完成, 共导出{}条", dataList.size());
        } catch (IOException e) {
            log.error("导出业务资源绑定数据异常", e);
            throw new RuntimeException("导出业务资源绑定数据异常", e);
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
     * 将BusinessResourceBinding转换为BusinessResourceBindingVO
     *
     * @param binding 业务资源绑定实体
     * @return 业务资源绑定VO
     */
    private BusinessResourceBindingVO convertToVO(BusinessResourceBinding binding) {
        if (binding == null) {
            return null;
        }
        BusinessResourceBindingVO vo = new BusinessResourceBindingVO();
        BeanUtils.copyProperties(binding, vo);
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
