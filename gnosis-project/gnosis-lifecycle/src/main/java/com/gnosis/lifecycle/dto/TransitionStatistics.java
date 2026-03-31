package com.gnosis.lifecycle.dto;

import java.io.Serializable;

/**
 * 流转统计 VO
 */
public class TransitionStatistics implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 流转编码
     */
    private String transitionCode;
    
    /**
     * 流转名称
     */
    private String transitionName;
    
    /**
     * 流转次数
     */
    private Integer transitionCount;
    
    /**
     * 成功次数
     */
    private Integer successCount;
    
    /**
     * 失败次数
     */
    private Integer failedCount;
    
    /**
     * 平均耗时（毫秒）
     */
    private Long avgExecutionTimeMs;
    
    public String getTransitionCode() {
        return transitionCode;
    }
    
    public void setTransitionCode(String transitionCode) {
        this.transitionCode = transitionCode;
    }
    
    public String getTransitionName() {
        return transitionName;
    }
    
    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }
    
    public Integer getTransitionCount() {
        return transitionCount;
    }
    
    public void setTransitionCount(Integer transitionCount) {
        this.transitionCount = transitionCount;
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
    
    public Long getAvgExecutionTimeMs() {
        return avgExecutionTimeMs;
    }
    
    public void setAvgExecutionTimeMs(Long avgExecutionTimeMs) {
        this.avgExecutionTimeMs = avgExecutionTimeMs;
    }
}
