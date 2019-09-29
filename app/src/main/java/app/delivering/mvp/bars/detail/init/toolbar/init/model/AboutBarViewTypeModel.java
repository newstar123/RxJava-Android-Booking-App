package app.delivering.mvp.bars.detail.init.toolbar.init.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AboutBarViewTypeModel implements Parcelable{
    private String url;
    private boolean isVideoUrl;

    public AboutBarViewTypeModel(String url) {this.url = url;}

    protected AboutBarViewTypeModel() {}

    protected AboutBarViewTypeModel(Parcel in) {
        url = in.readString();
        isVideoUrl = in.readByte() != 0;
    }

    public static final Creator<AboutBarViewTypeModel> CREATOR = new Creator<AboutBarViewTypeModel>() {
        @Override
        public AboutBarViewTypeModel createFromParcel(Parcel in) {
            return new AboutBarViewTypeModel(in);
        }

        @Override
        public AboutBarViewTypeModel[] newArray(int size) {
            return new AboutBarViewTypeModel[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public boolean isVideoUrl() {
        return isVideoUrl;
    }

    public void setVideoUrl(boolean videoUrl) {
        isVideoUrl = videoUrl;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeByte((byte) (isVideoUrl ? 1 : 0));
    }
}
