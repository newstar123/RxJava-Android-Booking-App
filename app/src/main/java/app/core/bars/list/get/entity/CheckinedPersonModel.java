package app.core.bars.list.get.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckinedPersonModel implements Serializable {
    private long id;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("facebook_id") private long facebookId;
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("facebook_visible") private String isFacebookVisible;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(long facebookId) {
        this.facebookId = facebookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIsFacebookVisible() {
        return isFacebookVisible == null ? "on" : isFacebookVisible;
    }

    public void setIsFacebookVisible(String isFacebookVisible) {
        this.isFacebookVisible = isFacebookVisible;
    }
}
