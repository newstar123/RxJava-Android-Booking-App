package app.delivering.mvp.feedback.send.model;

import android.graphics.Bitmap;

public class ShareFeedbackResponse {
    private String profileRefCode;
    private Bitmap shareImage;
    private String path;

    public String getProfileRefCode() {
        return profileRefCode;
    }

    public void setProfileRefCode(String profileRefCode) {
        this.profileRefCode = profileRefCode;
    }

    public Bitmap getShareImage() {
        return shareImage;
    }

    public void setShareImage(Bitmap shareImage) {
        this.shareImage = shareImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
