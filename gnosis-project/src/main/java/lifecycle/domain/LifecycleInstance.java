package lifecycle.domain;

import lifecycle.domain.base.BaseEntity;
import java.util.Date;

/**
 * 生命周期实例实体
 * 对应表：lifecycle_instance
 */
public class LifecycleInstance extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 业务 ID
     */
    private String businessId;
    
    /**
     * 当前状态 ID
     */
    private String currentStateId;
    
    /**
     * 状态 (ACTIVE/COMPLETED/CANCELLED)
     */
    private String status;
    
    /**
     * 开始时间
     */
    private Date startedAt;
    
    /**
     * 结束时间
     */
    private Date finishedAt;
    
    /**
     * 终态 ID
     */
    private String finalStateId;
    
    /**
     * 业务实例 ID
     */
    private String businessInstanceId;
    
    /**
     * 当前状态编码
     */
    private String currentStateCode;
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    
    public String getCurrentStateId() {
        return currentStateId;
    }
    
    public void setCurrentStateId(String currentStateId) {
        this.currentStateId = currentStateId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }
    
    public Date getFinishedAt() {
        return finishedAt;
    }
    
    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }
    
    public String getFinalStateId() {
        return finalStateId;
    }
    
    public void setFinalStateId(String finalStateId) {
        this.finalStateId = finalStateId;
    }
    
    public String getBusinessInstanceId() {
        return businessInstanceId;
    }
    
    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }
    
    public String getCurrentStateCode() {
        return currentStateCode;
    }
    
    public void setCurrentStateCode(String currentStateCode) {
        this.currentStateCode = currentStateCode;
    }
}
