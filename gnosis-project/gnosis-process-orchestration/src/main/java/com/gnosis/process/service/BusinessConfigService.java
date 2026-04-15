package com.gnosis.process.service;

import com.gnosis.process.domain.BusinessConfig;
import com.gnosis.process.dto.BusinessConfigCreateRequest;
import com.gnosis.process.dto.BusinessConfigQueryRequest;
import com.gnosis.process.dto.BusinessConfigUpdateRequest;
import com.gnosis.process.dto.BusinessConfigVO;
import com.gnosis.process.dto.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 业务配置 Service 接口
 */
public interface BusinessConfigService {

    /**
     * 根据ID获取业务配置
     *
     * @param id 业务配置ID
     * @return 业务配置实体
     */
    BusinessConfig getById(String id);

    /**
     * 根据编码获取业务配置
     *
     * @param code 业务配置编码
     * @return 业务配置实体
     */
    BusinessConfig getByCode(String code);

    /**
     * 分页查询业务配置列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<BusinessConfigVO> list(BusinessConfigQueryRequest request);

    /**
     * 新增业务配置
     *
     * @param request 新增请求参数
     * @return 影响行数
     */
    int save(BusinessConfigCreateRequest request);

    /**
     * 更新业务配置
     *
     * @param request 更新请求参数
     * @return 影响行数
     */
    int update(BusinessConfigUpdateRequest request);

    /**
     * 删除业务配置
     *
     * @param id 业务配置ID
     * @return 影响行数
     */
    int delete(String id);

    /**
     * 批量删除业务配置
     *
     * @param ids 业务配置ID列表
     * @return 影响行数
     */
    int batchDelete(List<String> ids);

    /**
     * 批量启用业务配置
     *
     * @param ids 业务配置ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchEnable(List<String> ids, String updateUserId);

    /**
     * 批量禁用业务配置
     *
     * @param ids 业务配置ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchDisable(List<String> ids, String updateUserId);

    /**
     * 导入业务配置数据
     *
     * @param file 导入文件
     * @param operatorId 操作人ID
     */
    void importData(MultipartFile file, String operatorId);

    /**
     * 导出业务配置数据
     *
     * @param request 查询请求参数
     * @param response HTTP响应
     */
    void exportData(BusinessConfigQueryRequest request, HttpServletResponse response);
}
