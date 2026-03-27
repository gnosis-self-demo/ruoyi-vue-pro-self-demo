package lifecycle.entry;

import lifecycle.domain.LifecycleBusinessType;
import lifecycle.dto.*;


import java.util.List;

/**
 * 入口服务接口
 */
public interface EntryService {

    /**
     * 注册业务入口
     *
     * @param request 创建请求
     * @return 业务类型 VO
     */
    BusinessTypeVO register(BusinessTypeCreateRequest request);

    /**
     * 更新业务入口
     *
     * @param request 更新请求
     * @return 业务类型 VO
     */
    BusinessTypeVO update(BusinessTypeUpdateRequest request);

    /**
     * 根据 ID 获取业务入口
     *
     * @param id 业务类型 ID
     * @return 业务类型 VO
     */
    BusinessTypeVO getById(String id);

    /**
     * 获取业务入口列表（分页）
     *
     * @param businessType 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 业务类型 VO 列表
     */
    List<BusinessTypeVO> list(LifecycleBusinessType businessType, int pageNum, int pageSize);

    /**
     * 删除业务入口
     *
     * @param id 业务类型 ID
     */
    void delete(String id);

    /**
     * 批量删除业务入口
     *
     * @param ids 业务类型 ID 数组
     */
    void batchDelete(String[] ids);

    /**
     * 批量启用业务入口
     *
     * @param ids 业务类型 ID 数组
     */
    void batchEnable(String[] ids);

    /**
     * 批量禁用业务入口
     *
     * @param ids 业务类型 ID 数组
     */
    void batchDisable(String[] ids);

    /**
     * 校验入口数据
     *
     * @param request 校验请求
     * @return 校验结果
     */
    EntryValidationResult validate(EntryValidationRequest request);
}
