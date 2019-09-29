package app.core.login.facebook.entity.picture;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Viacheslav Titov on 03.01.2018.
 */

public class Picture {

    @SerializedName("is_silhouette")
    private boolean isSilhouette;

    @SerializedName("url")
    private String url;

    private int height;
    private int width;

    public String getUrl() {
        return url;
    }

    public boolean isSilhouette() {
        return isSilhouette;
    }
}
