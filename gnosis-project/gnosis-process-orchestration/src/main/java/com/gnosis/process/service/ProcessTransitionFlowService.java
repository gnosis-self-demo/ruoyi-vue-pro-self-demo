package com.gnosis.process.service;

import com.gnosis.process.domain.ProcessTransitionFlow;
import com.gnosis.process.dto.ProcessTransitionFlowQueryRequest;
import com.gnosis.process.dto.ProcessTransitionFlowVO;
import com.gnosis.process.dto.PageResult;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 流程流转记录 Service 接口
 */
public interface ProcessTransitionFlowService {

    /**
     * 根据ID获取流程流转记录
     *
     * @param id 流程流转记录ID
     * @return 流程流转记录实体
     */
    ProcessTransitionFlow getById(String id);

    /**
     * 分页查询流程流转记录列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<ProcessTransitionFlowVO> list(ProcessTransitionFlowQueryRequest request);

    /**
     * 根据流程实例ID查询流程流转记录列表
     *
     * @param processInstanceId 流程实例ID
     * @return 流程流转记录VO列表
     */
    List<ProcessTransitionFlowVO> listByProcessInstanceId(String processInstanceId);

    /**
     * 根据业务ID查询流程流转记录列表
     *
     * @param businessId 业务ID
     * @return 流程流转记录VO列表
     */
    List<ProcessTransitionFlowVO> listByBusinessId(String businessId);

    /**
     * 导出流程流转记录数据
     *
     * @param request 查询请求参数
     * @param response HTTP响应
     */
    void exportData(ProcessTransitionFlowQueryRequest request, HttpServletResponse response);
}
