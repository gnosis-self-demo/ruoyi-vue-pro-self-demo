package com.gnosis.process.service;

import com.gnosis.process.domain.ProcessEventConfig;
import com.gnosis.process.dto.ProcessEventConfigCreateRequest;
import com.gnosis.process.dto.ProcessEventConfigQueryRequest;
import com.gnosis.process.dto.ProcessEventConfigUpdateRequest;
import com.gnosis.process.dto.ProcessEventConfigVO;
import com.gnosis.process.dto.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流程事件配置 Service 接口
 */
public interface ProcessEventConfigService {

    /**
     * 根据ID获取流程事件配置
     *
     * @param id 流程事件配置ID
     * @return 流程事件配置实体
     */
    ProcessEventConfig getById(String id);

    /**
     * 分页查询流程事件配置列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<ProcessEventConfigVO> list(ProcessEventConfigQueryRequest request);

    /**
     * 新增流程事件配置
     *
     * @param request 新增请求参数
     * @return 影响行数
     */
    int save(ProcessEventConfigCreateRequest request);

    /**
     * 更新流程事件配置
     *
     * @param request 更新请求参数
     * @return 影响行数
     */
    int update(ProcessEventConfigUpdateRequest request);

    /**
     * 删除流程事件配置
     *
     * @param id 流程事件配置ID
     * @return 影响行数
     */
    int delete(String id);

    /**
     * 批量删除流程事件配置
     *
     * @param ids 流程事件配置ID列表
     * @return 影响行数
     */
    int batchDelete(List<String> ids);

    /**
     * 批量启用流程事件配置
     *
     * @param ids 流程事件配置ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchEnable(List<String> ids, String updateUserId);

    /**
     * 批量禁用流程事件配置
     *
     * @param ids 流程事件配置ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchDisable(List<String> ids, String updateUserId);

    /**
     * 导入流程事件配置数据
     *
     * @param file 导入文件
     * @param operatorId 操作人ID
     */
    void importData(MultipartFile file, String operatorId);

    /**
     * 导出流程事件配置数据
     *
     * @param request 查询请求参数
     * @param response HTTP响应
     */
    void exportData(ProcessEventConfigQueryRequest request, HttpServletResponse response);
}
