package app.core.login.facebook.entity;


import com.google.gson.annotations.SerializedName;

public class SignUpRequest {
    @SerializedName("facebook_token") private String facebookToken;

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }
}
