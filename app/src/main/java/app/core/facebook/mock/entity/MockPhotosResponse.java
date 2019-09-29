package app.core.facebook.mock.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MockPhotosResponse {
    @SerializedName("success") private boolean isSuccess;
    @SerializedName("data") private List<String> photos;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
