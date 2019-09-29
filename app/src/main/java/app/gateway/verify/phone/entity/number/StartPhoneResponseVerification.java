package app.gateway.verify.phone.entity.number;

import com.google.gson.annotations.SerializedName;

public class StartPhoneResponseVerification {
    @SerializedName("carrier") private String carrier;
    @SerializedName("is_cellphone") private Boolean isCellphone;
    @SerializedName("message") private String message;
    @SerializedName("seconds_to_expire") private Integer secondsToExpire;
    @SerializedName("uuid") private String UUID;
    @SerializedName("success") private Boolean isSuccess;


    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Boolean getCellphone() {
        return isCellphone;
    }

    public void setCellphone(Boolean cellphone) {
        isCellphone = cellphone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSecondsToExpire() {
        return secondsToExpire;
    }

    public void setSecondsToExpire(Integer secondsToExpire) {
        this.secondsToExpire = secondsToExpire;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
