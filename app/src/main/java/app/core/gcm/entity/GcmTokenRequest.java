package app.core.gcm.entity;

import com.google.gson.annotations.SerializedName;

public class GcmTokenRequest {
    @SerializedName("mobile_id")
    private String mobileId;
    @SerializedName("mobile_platform")
    private String mobilePlatform;

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getMobilePlatform() {
        return mobilePlatform;
    }

    public void setMobilePlatform(String mobilePlatform) {
        this.mobilePlatform = mobilePlatform;
    }
}
