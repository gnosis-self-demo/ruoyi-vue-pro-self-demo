package paramcheck.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * 校验日志实体 (映射 sys_validation_logs)
 */
public class ValidationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long logId;
    private String flowId;
    private String requestId;
    private String modeType;
    private Object inputSnapshot;
    private String failedNode;
    private String errorMsg;
    private OffsetDateTime createdTime;

    // Getter and Setter methods
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public Object getInputSnapshot() {
        return inputSnapshot;
    }

    public void setInputSnapshot(Object inputSnapshot) {
        this.inputSnapshot = inputSnapshot;
    }

    public String getFailedNode() {
        return failedNode;
    }

    public void setFailedNode(String failedNode) {
        this.failedNode = failedNode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public OffsetDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(OffsetDateTime createdTime) {
        this.createdTime = createdTime;
    }
}
