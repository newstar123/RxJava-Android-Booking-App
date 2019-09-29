package app.core.profile.put.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PutProfileModel implements Parcelable {
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    private String gender;
    private String birthdate;
    private String email;
    private String zip;
    private String phone;

    public PutProfileModel(){}

    protected PutProfileModel(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        gender = in.readString();
        email = in.readString();
        zip = in.readString();
        phone = in.readString();
    }

    public static final Creator<PutProfileModel> CREATOR = new Creator<PutProfileModel>() {
        @Override
        public PutProfileModel createFromParcel(Parcel in) {
            return new PutProfileModel(in);
        }

        @Override
        public PutProfileModel[] newArray(int size) {
            return new PutProfileModel[size];
        }
    };

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }

    public String getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(gender);
        dest.writeString(email);
        dest.writeString(zip);
        dest.writeString(phone);
    }
}
