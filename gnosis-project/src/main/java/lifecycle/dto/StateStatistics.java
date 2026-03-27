package lifecycle.dto;

import java.io.Serializable;

/**
 * 状态统计 VO
 */
public class StateStatistics implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态 ID
     */
    private String stateId;
    
    /**
     * 状态编码
     */
    private String stateCode;
    
    /**
     * 状态名称
     */
    private String stateName;
    
    /**
     * 实例数量
     */
    private Integer instanceCount;
    
    /**
     * 今日新增数量
     */
    private Integer todayCount;
    
    public String getStateId() {
        return stateId;
    }
    
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }
    
    public String getStateCode() {
        return stateCode;
    }
    
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
    
    public String getStateName() {
        return stateName;
    }
    
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
    
    public Integer getInstanceCount() {
        return instanceCount;
    }
    
    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }
    
    public Integer getTodayCount() {
        return todayCount;
    }
    
    public void setTodayCount(Integer todayCount) {
        this.todayCount = todayCount;
    }
}
