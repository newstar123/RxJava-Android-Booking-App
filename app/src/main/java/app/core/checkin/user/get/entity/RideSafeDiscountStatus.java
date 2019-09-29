package app.core.checkin.user.get.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class RideSafeDiscountStatus implements Parcelable {
    private RideSafeDiscountTime time;
    private int minSpendToRideDiscount;
    private boolean isFreeRideAvailable;

    protected RideSafeDiscountStatus(Parcel in) {
        time = in.readParcelable(RideSafeDiscountTime.class.getClassLoader());
        minSpendToRideDiscount = in.readInt();
        isFreeRideAvailable = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(time, flags);
        dest.writeInt(minSpendToRideDiscount);
        dest.writeByte((byte) (isFreeRideAvailable ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RideSafeDiscountStatus> CREATOR = new Creator<RideSafeDiscountStatus>() {
        @Override
        public RideSafeDiscountStatus createFromParcel(Parcel in) {
            return new RideSafeDiscountStatus(in);
        }

        @Override
        public RideSafeDiscountStatus[] newArray(int size) {
            return new RideSafeDiscountStatus[size];
        }
    };

    public RideSafeDiscountTime getTime() {
        return time;
    }

    public void setTime(RideSafeDiscountTime time) {
        this.time = time;
    }

    public int getMinSpendToRideDiscount() {
        return minSpendToRideDiscount;
    }

    public void setMinSpendToRideDiscount(int minSpendToRideDiscount) {
        this.minSpendToRideDiscount = minSpendToRideDiscount;
    }

    public boolean isFreeRideAvailable() {
        return isFreeRideAvailable;
    }

    public void setFreeRideAvailable(boolean freeRideAvailable) {
        isFreeRideAvailable = freeRideAvailable;
    }
}
