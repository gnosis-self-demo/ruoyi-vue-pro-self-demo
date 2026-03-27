package lifecycle.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 追踪看板数据 VO
 */
public class TraceDashboardVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 总实例数
     */
    private Integer totalInstances;
    
    /**
     * 今日新增实例数
     */
    private Integer todayInstances;
    
    /**
     * 总事件数
     */
    private Integer totalEvents;
    
    /**
     * 今日事件数
     */
    private Integer todayEvents;
    
    /**
     * 状态统计列表
     */
    private List<StateStatistics> stateStatistics;
    
    /**
     * 事件统计列表
     */
    private List<EventStatistics> eventStatistics;
    
    /**
     * 流转统计列表
     */
    private List<TransitionStatistics> transitionStatistics;
    
    public Integer getTotalInstances() {
        return totalInstances;
    }
    
    public void setTotalInstances(Integer totalInstances) {
        this.totalInstances = totalInstances;
    }
    
    public Integer getTodayInstances() {
        return todayInstances;
    }
    
    public void setTodayInstances(Integer todayInstances) {
        this.todayInstances = todayInstances;
    }
    
    public Integer getTotalEvents() {
        return totalEvents;
    }
    
    public void setTotalEvents(Integer totalEvents) {
        this.totalEvents = totalEvents;
    }
    
    public Integer getTodayEvents() {
        return todayEvents;
    }
    
    public void setTodayEvents(Integer todayEvents) {
        this.todayEvents = todayEvents;
    }
    
    public List<StateStatistics> getStateStatistics() {
        return stateStatistics;
    }
    
    public void setStateStatistics(List<StateStatistics> stateStatistics) {
        this.stateStatistics = stateStatistics;
    }
    
    public List<EventStatistics> getEventStatistics() {
        return eventStatistics;
    }
    
    public void setEventStatistics(List<EventStatistics> eventStatistics) {
        this.eventStatistics = eventStatistics;
    }
    
    public List<TransitionStatistics> getTransitionStatistics() {
        return transitionStatistics;
    }
    
    public void setTransitionStatistics(List<TransitionStatistics> transitionStatistics) {
        this.transitionStatistics = transitionStatistics;
    }
}
