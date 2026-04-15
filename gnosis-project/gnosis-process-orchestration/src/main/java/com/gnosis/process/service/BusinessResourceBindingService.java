package com.gnosis.process.service;

import com.gnosis.process.domain.BusinessResourceBinding;
import com.gnosis.process.dto.BusinessResourceBindingCreateRequest;
import com.gnosis.process.dto.BusinessResourceBindingQueryRequest;
import com.gnosis.process.dto.BusinessResourceBindingUpdateRequest;
import com.gnosis.process.dto.BusinessResourceBindingVO;
import com.gnosis.process.dto.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 业务资源绑定 Service 接口
 */
public interface BusinessResourceBindingService {

    /**
     * 根据ID获取业务资源绑定
     *
     * @param id 业务资源绑定ID
     * @return 业务资源绑定实体
     */
    BusinessResourceBinding getById(String id);

    /**
     * 分页查询业务资源绑定列表
     *
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResult<BusinessResourceBindingVO> list(BusinessResourceBindingQueryRequest request);

    /**
     * 根据业务配置ID查询业务资源绑定列表
     *
     * @param businessConfigId 业务配置ID
     * @return 业务资源绑定VO列表
     */
    List<BusinessResourceBindingVO> listByBusinessConfigId(String businessConfigId);

    /**
     * 根据业务编码和资源类型查询业务资源绑定列表
     *
     * @param businessCode 业务编码
     * @param resourceType 资源类型
     * @return 业务资源绑定VO列表
     */
    List<BusinessResourceBindingVO> listByBusinessCodeAndType(String businessCode, String resourceType);

    /**
     * 新增业务资源绑定
     *
     * @param request 新增请求参数
     * @return 影响行数
     */
    int save(BusinessResourceBindingCreateRequest request);

    /**
     * 更新业务资源绑定
     *
     * @param request 更新请求参数
     * @return 影响行数
     */
    int update(BusinessResourceBindingUpdateRequest request);

    /**
     * 删除业务资源绑定
     *
     * @param id 业务资源绑定ID
     * @return 影响行数
     */
    int delete(String id);

    /**
     * 批量删除业务资源绑定
     *
     * @param ids 业务资源绑定ID列表
     * @return 影响行数
     */
    int batchDelete(List<String> ids);

    /**
     * 批量启用业务资源绑定
     *
     * @param ids 业务资源绑定ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchEnable(List<String> ids, String updateUserId);

    /**
     * 批量禁用业务资源绑定
     *
     * @param ids 业务资源绑定ID列表
     * @param updateUserId 更新人ID
     * @return 影响行数
     */
    int batchDisable(List<String> ids, String updateUserId);

    /**
     * 导入业务资源绑定数据
     *
     * @param file 导入文件
     * @param operatorId 操作人ID
     */
    void importData(MultipartFile file, String operatorId);

    /**
     * 导出业务资源绑定数据
     *
     * @param request 查询请求参数
     * @param response HTTP响应
     */
    void exportData(BusinessResourceBindingQueryRequest request, HttpServletResponse response);
}
