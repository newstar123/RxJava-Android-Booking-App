package app.delivering.mvp.tab.close.init.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FillCloseTabActivityModel implements Parcelable {

    private double discount;
    private long vendorId;
    private long checkInId;
    private boolean isTabAutoClosed;

    public FillCloseTabActivityModel() { }

    protected FillCloseTabActivityModel(Parcel in)  {
        discount = in.readDouble();
        vendorId = in.readLong();
        checkInId = in.readLong();
        isTabAutoClosed = in.readByte() != 0;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public long getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(long checkInId) {
        this.checkInId = checkInId;
    }

    public boolean isTabAutoClosed() {
        return isTabAutoClosed;
    }

    public void setTabAutoClosed(boolean tabAutoClosed) {
        isTabAutoClosed = tabAutoClosed;
    }

    public static final Creator<FillCloseTabActivityModel> CREATOR = new Creator<FillCloseTabActivityModel>() {
        @Override
        public FillCloseTabActivityModel createFromParcel(Parcel in) {
            return new FillCloseTabActivityModel(in);
        }

        @Override
        public FillCloseTabActivityModel[] newArray(int size) {
            return new FillCloseTabActivityModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.discount);
        dest.writeLong(this.vendorId);
        dest.writeLong(this.checkInId);
        dest.writeByte((byte) (isTabAutoClosed ? 1 : 0 ));
    }
}
