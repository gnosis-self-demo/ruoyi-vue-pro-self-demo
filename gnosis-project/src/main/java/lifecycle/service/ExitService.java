package lifecycle.service;

import lifecycle.dto.DownstreamConfigRequest;
import lifecycle.dto.DownstreamConfigVO;
import lifecycle.dto.EventPublishRequest;
import lifecycle.dto.FinalStateConfigRequest;
import paramcheck.lifecycle.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 出口服务接口
 */
public interface ExitService {
    
    /**
     * 配置终态
     */
    Map<String, Object> configureFinalState(FinalStateConfigRequest request, String operatorId);
    
    /**
     * 获取终态配置列表
     */
    List<Map<String, Object>> listFinalStates(String businessTypeId);
    
    /**
     * 配置下游触发
     */
    Map<String, Object> configureDownstream(DownstreamConfigRequest request, String operatorId);
    
    /**
     * 获取下游配置
     */
    DownstreamConfigVO getDownstreamConfig(String id);
    
    /**
     * 更新下游配置
     */
    Map<String, Object> updateDownstream(DownstreamConfigRequest request, String operatorId);
    
    /**
     * 删除下游配置
     */
    Map<String, Object> deleteDownstream(String id, String operatorId);
    
    /**
     * 批量删除下游配置
     */
    Map<String, Object> batchDeleteDownstream(String[] ids, String operatorId);
    
    /**
     * 发布事件
     */
    Map<String, Object> publishEvent(EventPublishRequest request, String operatorId);
    
    /**
     * 获取事件发布记录
     */
    Map<String, Object> getPublishRecord(String eventId);
}
