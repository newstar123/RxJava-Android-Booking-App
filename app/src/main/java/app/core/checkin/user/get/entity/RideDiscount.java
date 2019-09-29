package app.core.checkin.user.get.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RideDiscount implements Serializable, Parcelable {
    private long id;
    @SerializedName("ride_company") private String company;
    @SerializedName("created_at") private String createdAt;
    @SerializedName("updated_at") private String updatedAt;
    @SerializedName("discount_value") private double discountValue;
    @SerializedName("ride_id") private long rideId;
    @SerializedName("checkin_id") private long checkinId;

    public RideDiscount() {
    }

    protected RideDiscount(Parcel in) {
        id = in.readLong();
        company = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        discountValue = in.readDouble();
        rideId = in.readLong();
        checkinId = in.readLong();
    }

    public static final Creator<RideDiscount> CREATOR = new Creator<RideDiscount>() {
        @Override
        public RideDiscount createFromParcel(Parcel in) {
            return new RideDiscount(in);
        }

        @Override
        public RideDiscount[] newArray(int size) {
            return new RideDiscount[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(company);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
        parcel.writeDouble(discountValue);
        parcel.writeLong(rideId);
        parcel.writeLong(checkinId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(int discountValue) {
        this.discountValue = discountValue;
    }

    public long getRideId() {
        return rideId;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public long getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(long checkinId) {
        this.checkinId = checkinId;
    }
}
