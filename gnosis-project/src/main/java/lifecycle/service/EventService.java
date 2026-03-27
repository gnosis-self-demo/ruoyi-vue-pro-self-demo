package lifecycle.service;

import lifecycle.dto.*;


import java.util.List;
import java.util.Map;

/**
 * 事件服务接口
 */
public interface EventService {

    /**
     * 提交事件
     *
     * @param request 事件提交请求
     * @return 事件 VO
     */
    EventVO submitEvent(EventSubmitRequest request);

    /**
     * 根据 ID 查询事件
     *
     * @param id 事件 ID
     * @return 事件 VO
     */
    EventVO getById(String id);

    /**
     * 查询事件列表
     *
     * @param businessTypeId 业务类型 ID
     * @param eventType 事件类型
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 事件列表 VO
     */
    List<EventVO> list(String businessTypeId, String eventType, int pageNum, int pageSize);

    /**
     * 测试路由规则
     *
     * @param request 路由测试请求
     * @return 路由测试结果 VO
     */
    RouteTestResultVO testRoute(RouteTestRequest request);

    /**
     * 执行状态流转
     *
     * @param request 流转执行请求
     * @return 流转结果
     */
    Map<String, Object> executeTransition(TransitionExecutionRequest request);

    /**
     * 获取事件元数据字典
     *
     * @return 元数据字典
     */
    Map<String, Object> getMetadataDictionary();
}
