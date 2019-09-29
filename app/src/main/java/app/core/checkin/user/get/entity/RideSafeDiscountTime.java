package app.core.checkin.user.get.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class RideSafeDiscountTime implements Parcelable {
    private long timePastFromFirstOrderInSecs;
    private long timeLeftToRideDiscount;
    private long ridesafeMinTimeSecs;

    protected RideSafeDiscountTime(Parcel in) {
        timePastFromFirstOrderInSecs = in.readLong();
        timeLeftToRideDiscount = in.readLong();
        ridesafeMinTimeSecs = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timePastFromFirstOrderInSecs);
        dest.writeLong(timeLeftToRideDiscount);
        dest.writeLong(ridesafeMinTimeSecs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RideSafeDiscountTime> CREATOR = new Creator<RideSafeDiscountTime>() {
        @Override
        public RideSafeDiscountTime createFromParcel(Parcel in) {
            return new RideSafeDiscountTime(in);
        }

        @Override
        public RideSafeDiscountTime[] newArray(int size) {
            return new RideSafeDiscountTime[size];
        }
    };

    public long getTimePastFromFirstOrderInSecs() {
        return timePastFromFirstOrderInSecs;
    }

    public void setTimePastFromFirstOrderInSecs(long timePastFromFirstOrderInSecs) {
        this.timePastFromFirstOrderInSecs = timePastFromFirstOrderInSecs;
    }

    public long getTimeLeftToRideDiscount() {
        return timeLeftToRideDiscount;
    }

    public void setTimeLeftToRideDiscount(long timeLeftToRideDiscount) {
        this.timeLeftToRideDiscount = timeLeftToRideDiscount;
    }

    public long getRidesafeMinTimeSecs() {
        return ridesafeMinTimeSecs;
    }

    public void setRidesafeMinTimeSecs(long ridesafeMinTimeSecs) {
        this.ridesafeMinTimeSecs = ridesafeMinTimeSecs;
    }
}
