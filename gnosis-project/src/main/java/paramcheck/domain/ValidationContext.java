package paramcheck.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 校验上下文 — 在 FLOW/HANDLER/HYBRID 全模式中传递数据
 */
public class ValidationContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 请求唯一标识 */
    private String requestId;

    /** 原始请求数据 (可以是 Map/JSON String/任意对象) */
    private Object rawRequest;

    /** 当前执行的流程 ID */
    private String flowId;

    /** 校验上下文数据槽 (组件之间共享数据) */
    private Map<String, Object> dataMap;

    /** LiteFlow 原生上下文 (LiteFlowResponse.getContextBean 返回的对象) */
    private Object liteflowContext;

    public ValidationContext() {
        this.requestId = UUID.randomUUID().toString().replace("-", "");
        this.dataMap = new HashMap<>();
    }

    public void setData(String key, Object value) {
        if (this.dataMap == null) {
            this.dataMap = new HashMap<>();
        }
        this.dataMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return dataMap == null ? null : (T) dataMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key, T defaultValue) {
        T val = getData(key);
        return val != null ? val : defaultValue;
    }

    // Getter and Setter methods
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getRawRequest() {
        return rawRequest;
    }

    public void setRawRequest(Object rawRequest) {
        this.rawRequest = rawRequest;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Object getLiteflowContext() {
        return liteflowContext;
    }

    public void setLiteflowContext(Object liteflowContext) {
        this.liteflowContext = liteflowContext;
    }
}
