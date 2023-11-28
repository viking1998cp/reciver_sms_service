package atis.vn.smsnotification.api.response;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    @SerializedName("status_code")
    protected String errorCode;


    @SerializedName("data")
    protected Boolean isSuccess;
}
