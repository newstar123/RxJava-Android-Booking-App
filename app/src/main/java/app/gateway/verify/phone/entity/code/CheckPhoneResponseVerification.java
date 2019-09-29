package app.gateway.verify.phone.entity.code;

import com.google.gson.annotations.SerializedName;

public class CheckPhoneResponseVerification {
    @SerializedName("message") private String message;
    @SerializedName("success") private Boolean isSuccess;

    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean success) {
        isSuccess = success;
    }
}
