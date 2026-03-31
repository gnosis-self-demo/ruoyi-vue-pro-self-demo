package com.gnosis.lifecycle.engine.transition;

import java.io.Serializable;
import java.util.Map;

/**
 * 流转上下文
 * 包含状态流转执行所需的所有信息
 */
public class TransitionContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务类型 ID
     */
    private String businessTypeId;

    /**
     * 业务实例 ID
     */
    private String businessInstanceId;

    /**
     * 流转 ID
     */
    private String transitionId;

    /**
     * 流转编码
     */
    private String transitionCode;

    /**
     * 源状态 ID
     */
    private String fromStateId;

    /**
     * 源状态编码
     */
    private String fromStateCode;

    /**
     * 目标状态 ID
     */
    private String toStateId;

    /**
     * 目标状态编码
     */
    private String toStateCode;

    /**
     * 流转数据
     */
    private Map<String, Object> transitionData;

    /**
     * 操作人 ID
     */
    private String operatorId;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 执行结果
     */
    private TransitionResult result;

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getBusinessInstanceId() {
        return businessInstanceId;
    }

    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }

    public String getTransitionId() {
        return transitionId;
    }

    public void setTransitionId(String transitionId) {
        this.transitionId = transitionId;
    }

    public String getTransitionCode() {
        return transitionCode;
    }

    public void setTransitionCode(String transitionCode) {
        this.transitionCode = transitionCode;
    }

    public String getFromStateId() {
        return fromStateId;
    }

    public void setFromStateId(String fromStateId) {
        this.fromStateId = fromStateId;
    }

    public String getFromStateCode() {
        return fromStateCode;
    }

    public void setFromStateCode(String fromStateCode) {
        this.fromStateCode = fromStateCode;
    }

    public String getToStateId() {
        return toStateId;
    }

    public void setToStateId(String toStateId) {
        this.toStateId = toStateId;
    }

    public String getToStateCode() {
        return toStateCode;
    }

    public void setToStateCode(String toStateCode) {
        this.toStateCode = toStateCode;
    }

    public Map<String, Object> getTransitionData() {
        return transitionData;
    }

    public void setTransitionData(Map<String, Object> transitionData) {
        this.transitionData = transitionData;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public TransitionResult getResult() {
        return result;
    }

    public void setResult(TransitionResult result) {
        this.result = result;
    }

    /**
     * 流转结果
     */
    public static class TransitionResult implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 结果消息
         */
        private String message;

        /**
         * 错误码
         */
        private String errorCode;

        /**
         * 流转后的状态 ID
         */
        private String newStateId;

        /**
         * 流转后的状态编码
         */
        private String newStateCode;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getNewStateId() {
            return newStateId;
        }

        public void setNewStateId(String newStateId) {
            this.newStateId = newStateId;
        }

        public String getNewStateCode() {
            return newStateCode;
        }

        public void setNewStateCode(String newStateCode) {
            this.newStateCode = newStateCode;
        }

        /**
         * 创建成功结果
         */
        public static TransitionResult success(String newStateId, String newStateCode, String message) {
            TransitionResult result = new TransitionResult();
            result.setSuccess(true);
            result.setNewStateId(newStateId);
            result.setNewStateCode(newStateCode);
            result.setMessage(message);
            return result;
        }

        /**
         * 创建失败结果
         */
        public static TransitionResult failure(String errorCode, String message) {
            TransitionResult result = new TransitionResult();
            result.setSuccess(false);
            result.setErrorCode(errorCode);
            result.setMessage(message);
            return result;
        }
    }
}
