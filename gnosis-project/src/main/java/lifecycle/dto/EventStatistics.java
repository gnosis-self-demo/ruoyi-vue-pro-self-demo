package lifecycle.dto;

import java.io.Serializable;

/**
 * 事件统计 VO
 */
public class EventStatistics implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 事件总数
     */
    private Integer totalCount;
    
    /**
     * 今日事件数
     */
    private Integer todayCount;
    
    /**
     * 成功数
     */
    private Integer successCount;
    
    /**
     * 失败数
     */
    private Integer failedCount;
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Integer getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    
    public Integer getTodayCount() {
        return todayCount;
    }
    
    public void setTodayCount(Integer todayCount) {
        this.todayCount = todayCount;
    }
    
    public Integer getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
    
    public Integer getFailedCount() {
        return failedCount;
    }
    
    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }
}
