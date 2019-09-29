package app.core.login.facebook.entity;


import com.google.gson.annotations.SerializedName;

import app.core.login.facebook.entity.picture.PictureData;

public class FacebookProfileResponse {
    private long id;

    @SerializedName("age_range")
    private AgeRange ageRange;

    @SerializedName("gender")
    private String gender;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("picture")
    private PictureData pictureData;

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public PictureData getPictureData() {
        return pictureData;
    }

    public void setPictureData(PictureData pictureData) {
        this.pictureData = pictureData;
    }
}
