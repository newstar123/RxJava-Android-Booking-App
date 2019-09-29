package app.core.checkin.friends.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckinsFriendModel implements Serializable, Parcelable{
    private int id;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("facebook_id") private long facebookId;
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;

    public CheckinsFriendModel(){

    }

    private CheckinsFriendModel(Parcel in) {
        id = in.readInt();
        imageUrl = in.readString();
        facebookId = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(imageUrl);
        parcel.writeLong(facebookId);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
    }

    public static final Creator<CheckinsFriendModel> CREATOR = new Creator<CheckinsFriendModel>() {
        @Override
        public CheckinsFriendModel createFromParcel(Parcel in) {
            return new CheckinsFriendModel(in);
        }

        @Override
        public CheckinsFriendModel[] newArray(int size) {
            return new CheckinsFriendModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

}
