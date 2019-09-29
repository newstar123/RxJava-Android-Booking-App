package app.core.facebook.visibility.entity;

import com.google.gson.annotations.SerializedName;

public class FacebookVisibilityModel {

    @SerializedName("facebook_visible")
    private String facebookVisible;

    public String getFacebookVisible() {
        return facebookVisible;
    }

    public void setFacebookVisible(String facebookVisible) {
        this.facebookVisible = facebookVisible;
    }
}
