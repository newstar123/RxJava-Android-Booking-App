package app.core.login.facebook.entity.picture;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Viacheslav Titov on 03.01.2018.
 */

public class PictureData {

    @SerializedName("data")
    private Picture picture;

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }
}
