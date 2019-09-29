package app.delivering.mvp.tab.init.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InitialTabActivityModel implements Parcelable {
    private String barName;
    private long checkInId;
    private long barId;
    private boolean isPhoneVerificationNeeded;

    public InitialTabActivityModel(){}

    protected InitialTabActivityModel(Parcel in) {
        barName = in.readString();
        checkInId = in.readLong();
        barId = in.readLong();
        isPhoneVerificationNeeded = in.readByte() != 0;
    }

    public static final Creator<InitialTabActivityModel> CREATOR = new Creator<InitialTabActivityModel>() {
        @Override
        public InitialTabActivityModel createFromParcel(Parcel in) {
            return new InitialTabActivityModel(in);
        }

        @Override
        public InitialTabActivityModel[] newArray(int size) {
            return new InitialTabActivityModel[size];
        }
    };

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public long getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(long checkInId) {
        this.checkInId = checkInId;
    }

    public long getBarId() {
        return barId;
    }

    public void setBarId(long barId) {
        this.barId = barId;
    }

    public boolean isPhoneVerificationNeeded() {
        return isPhoneVerificationNeeded;
    }

    public void setPhoneVerificationNeeded(boolean phoneVerificationNeeded) {
        isPhoneVerificationNeeded = phoneVerificationNeeded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barName);
        dest.writeLong(checkInId);
        dest.writeLong(barId);
        dest.writeByte((byte) (isPhoneVerificationNeeded ? 1 : 0));
    }
}
