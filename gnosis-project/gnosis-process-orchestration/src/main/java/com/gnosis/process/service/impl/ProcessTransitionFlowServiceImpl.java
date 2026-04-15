package com.gnosis.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.process.domain.ProcessTransitionFlow;
import com.gnosis.process.dto.ProcessTransitionFlowQueryRequest;
import com.gnosis.process.dto.ProcessTransitionFlowVO;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.mapper.ProcessTransitionFlowMapper;
import com.gnosis.process.service.ProcessTransitionFlowService;

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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程流转记录 Service 实现类（只读）
 */
@Service
public class ProcessTransitionFlowServiceImpl implements ProcessTransitionFlowService {

    private static final Logger log = LoggerFactory.getLogger(ProcessTransitionFlowServiceImpl.class);

    @Autowired
    private ProcessTransitionFlowMapper processTransitionFlowMapper;

    @Override
    public ProcessTransitionFlow getById(String id) {
        log.info("根据ID获取流程流转记录, id={}", id);
        return processTransitionFlowMapper.selectById(id);
    }

    @Override
    public PageResult<ProcessTransitionFlowVO> list(ProcessTransitionFlowQueryRequest request) {
        log.info("分页查询流程流转记录列表, request={}", JSON.toJSONString(request));
        Long total = processTransitionFlowMapper.selectCount(request);
        List<ProcessTransitionFlow> records = processTransitionFlowMapper.selectList(request);
        List<ProcessTransitionFlowVO> voList = new ArrayList<ProcessTransitionFlowVO>();
        for (ProcessTransitionFlow flow : records) {
            voList.add(convertToVO(flow));
        }
        return new PageResult<ProcessTransitionFlowVO>(voList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<ProcessTransitionFlowVO> listByProcessInstanceId(String processInstanceId) {
        log.info("根据流程实例ID查询流程流转记录列表, processInstanceId={}", processInstanceId);
        List<ProcessTransitionFlow> flows = processTransitionFlowMapper.selectByProcessInstanceId(processInstanceId);
        List<ProcessTransitionFlowVO> voList = new ArrayList<ProcessTransitionFlowVO>();
        for (ProcessTransitionFlow flow : flows) {
            voList.add(convertToVO(flow));
        }
        return voList;
    }

    @Override
    public List<ProcessTransitionFlowVO> listByBusinessId(String businessId) {
        log.info("根据业务ID查询流程流转记录列表, businessId={}", businessId);
        List<ProcessTransitionFlow> flows = processTransitionFlowMapper.selectByBusinessId(businessId);
        List<ProcessTransitionFlowVO> voList = new ArrayList<ProcessTransitionFlowVO>();
        for (ProcessTransitionFlow flow : flows) {
            voList.add(convertToVO(flow));
        }
        return voList;
    }

    @Override
    public void exportData(ProcessTransitionFlowQueryRequest request, HttpServletResponse response) {
        log.info("导出流程流转记录数据, request={}", JSON.toJSONString(request));
        List<ProcessTransitionFlow> dataList = processTransitionFlowMapper.selectList(request);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("流程流转记录");

        // 写表头
        String[] headers = {"编排配置ID", "事件配置ID", "流程定义键", "流程定义唯一键", "节点定义键",
                "流程实例ID", "流程实例序号", "业务描述", "业务ID", "来源节点", "目标节点",
                "流转类型", "流转结果", "备注", "创建人ID", "更新人ID", "创建时间", "更新时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 写数据行
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < dataList.size(); i++) {
            ProcessTransitionFlow flow = dataList.get(i);
            Row dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(flow.getOrchestrationConfigId() != null ? flow.getOrchestrationConfigId() : "");
            dataRow.createCell(1).setCellValue(flow.getEventConfigId() != null ? flow.getEventConfigId() : "");
            dataRow.createCell(2).setCellValue(flow.getProcessDefKey() != null ? flow.getProcessDefKey() : "");
            dataRow.createCell(3).setCellValue(flow.getProcessDefUniqueKey() != null ? flow.getProcessDefUniqueKey() : "");
            dataRow.createCell(4).setCellValue(flow.getNodeDefKey() != null ? flow.getNodeDefKey() : "");
            dataRow.createCell(5).setCellValue(flow.getProcessInstanceId() != null ? flow.getProcessInstanceId() : "");
            dataRow.createCell(6).setCellValue(flow.getProcessInstanceSequence() != null ? flow.getProcessInstanceSequence().toString() : "");
            dataRow.createCell(7).setCellValue(flow.getBusinessDescription() != null ? flow.getBusinessDescription() : "");
            dataRow.createCell(8).setCellValue(flow.getBusinessId() != null ? flow.getBusinessId() : "");
            dataRow.createCell(9).setCellValue(flow.getFromNode() != null ? flow.getFromNode() : "");
            dataRow.createCell(10).setCellValue(flow.getToNode() != null ? flow.getToNode() : "");
            dataRow.createCell(11).setCellValue(flow.getTransitionType() != null ? flow.getTransitionType() : "");
            dataRow.createCell(12).setCellValue(flow.getTransitionResult() != null ? flow.getTransitionResult() : "");
            dataRow.createCell(13).setCellValue(flow.getRemarks() != null ? flow.getRemarks() : "");
            dataRow.createCell(14).setCellValue(flow.getCreateUserId() != null ? flow.getCreateUserId() : "");
            dataRow.createCell(15).setCellValue(flow.getUpdateUserId() != null ? flow.getUpdateUserId() : "");
            dataRow.createCell(16).setCellValue(flow.getCreateTime() != null ? sdf.format(flow.getCreateTime()) : "");
            dataRow.createCell(17).setCellValue(flow.getUpdateTime() != null ? sdf.format(flow.getUpdateTime()) : "");
        }

        // 写入响应
        OutputStream outputStream = null;
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = URLEncoder.encode("流程流转记录.xlsx", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            log.info("导出流程流转记录数据完成, 共导出{}条", dataList.size());
        } catch (IOException e) {
            log.error("导出流程流转记录数据异常", e);
            throw new RuntimeException("导出流程流转记录数据异常", e);
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
     * 将ProcessTransitionFlow转换为ProcessTransitionFlowVO
     *
     * @param flow 流程流转记录实体
     * @return 流程流转记录VO
     */
    private ProcessTransitionFlowVO convertToVO(ProcessTransitionFlow flow) {
        if (flow == null) {
            return null;
        }
        ProcessTransitionFlowVO vo = new ProcessTransitionFlowVO();
        BeanUtils.copyProperties(flow, vo);
        return vo;
    }
}
