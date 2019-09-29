package app.gateway.verify.phone.entity.code;

import com.google.gson.annotations.SerializedName;

public class CheckPhoneRequestVerification {
    @SerializedName("phone_number") private String phoneNumber;
    @SerializedName("country_code") private String countryCode;
    @SerializedName("verification_code") private String verificationCode;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
