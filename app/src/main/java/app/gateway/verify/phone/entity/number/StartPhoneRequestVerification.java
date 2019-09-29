package app.gateway.verify.phone.entity.number;

import com.google.gson.annotations.SerializedName;

public class StartPhoneRequestVerification {
    @SerializedName("phone_number") private String phoneNumber;
    @SerializedName("country_code") private String countryCode;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
