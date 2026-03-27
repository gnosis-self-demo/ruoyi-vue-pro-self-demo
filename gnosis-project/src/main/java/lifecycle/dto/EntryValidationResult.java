package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 入口校验结果 DTO
 */
@ApiModel(value = "EntryValidationResult", description = "入口校验结果")
public class EntryValidationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否通过校验", required = true, example = "true")
    private boolean passed;

    @ApiModelProperty(value = "校验消息", example = "校验通过")
    private String message;

    @ApiModelProperty(value = "错误码", example = "SUCCESS")
    private String errorCode;

    @ApiModelProperty(value = "业务类型名称", example = "订单管理")
    private String businessTypeName;

    @ApiModelProperty(value = "业务类型编码", example = "ORDER_MANAGE")
    private String businessTypeCode;

    public EntryValidationResult() {
    }

    public EntryValidationResult(boolean passed, String message) {
        this.passed = passed;
        this.message = message;
        this.errorCode = passed ? "SUCCESS" : "VALIDATION_FAILED";
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
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

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    public String getBusinessTypeCode() {
        return businessTypeCode;
    }

    public void setBusinessTypeCode(String businessTypeCode) {
        this.businessTypeCode = businessTypeCode;
    }

    public static EntryValidationResult success(String message) {
        return new EntryValidationResult(true, message);
    }

    public static EntryValidationResult fail(String message) {
        return new EntryValidationResult(false, message);
    }

    public static EntryValidationResult fail(String errorCode, String message) {
        EntryValidationResult result = new EntryValidationResult(false, message);
        result.setErrorCode(errorCode);
        return result;
    }
}
