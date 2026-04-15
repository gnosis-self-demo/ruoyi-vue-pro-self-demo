package com.gnosis.process.service;

import com.gnosis.process.domain.ProcessOrchestrationConfig;
import com.gnosis.process.dto.ProcessOrchestrationConfigCreateRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigQueryRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigUpdateRequest;
import com.gnosis.process.dto.ProcessOrchestrationConfigVO;
import com.gnosis.process.dto.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流程编排配置 Service 接口
 */
public interface ProcessOrchestrationConfigService {

    /**
     * 根据ID获取流程编排配置
     *
     * @param id 流程编排配置ID
     * @return 流程编排配置实体
     */
    ProcessOrchestrationConfig getById(String id);

    /**
     * 分页查询流程编排配置列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<ProcessOrchestrationConfigVO> list(ProcessOrchestrationConfigQueryRequest request);

    /**
     * 根据业务配置ID查询流程编排配置列表
     *
     * @param businessConfigId 业务配置ID
     * @return 流程编排配置VO列表
     */
    List<ProcessOrchestrationConfigVO> listByBusinessConfigId(String businessConfigId);

    /**
     * 新增流程编排配置
     *
     * @param request 新增请求参数
     * @return 影响行数
     */
    int save(ProcessOrchestrationConfigCreateRequest request);

    /**
     * 更新流程编排配置
     *
     * @param request 更新请求参数
     * @return 影响行数
     */
    int update(ProcessOrchestrationConfigUpdateRequest request);

    /**
     * 删除流程编排配置
     *
     * @param id 流程编排配置ID
     * @return 影响行数
     */
    int delete(String id);

    /**
     * 批量删除流程编排配置
     *
     * @param ids 流程编排配置ID列表
     * @return 影响行数
     */
    int batchDelete(List<String> ids);

    /**
     * 批量启用流程编排配置
     *
     * @param ids 流程编排配置ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchEnable(List<String> ids, String updateUserId);

    /**
     * 批量禁用流程编排配置
     *
     * @param ids 流程编排配置ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchDisable(List<String> ids, String updateUserId);

    /**
     * 导入流程编排配置数据
     *
     * @param file 导入文件
     * @param operatorId 操作人ID
     */
    void importData(MultipartFile file, String operatorId);

    /**
     * 导出流程编排配置数据
     *
     * @param request 查询请求参数
     * @param response HTTP响应
     */
    void exportData(ProcessOrchestrationConfigQueryRequest request, HttpServletResponse response);
}
