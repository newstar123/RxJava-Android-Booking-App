package app.delivering.mvp.bars.detail.init.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InitialVenueDetailModel implements Parcelable {
    private long barId;
    private double distanceKm;
    private String nameValue;
    private String type;
    private String discount;
    private String image;
    private String distance;
    private String swipingText;
    private String barWorkType;
    private boolean shouldAutoOpenTab;
    private boolean shouldOpenBySwiping;
    private boolean shouldChangePaymentMethod;
    private boolean shouldVerifyEmail;

    public InitialVenueDetailModel() {}

    protected InitialVenueDetailModel(Parcel in) {
        barId = in.readLong();
        distanceKm = in.readDouble();
        nameValue = in.readString();
        type = in.readString();
        discount = in.readString();
        image = in.readString();
        distance = in.readString();
        swipingText = in.readString();
        barWorkType = in.readString();
        shouldAutoOpenTab = in.readByte() != 0;
        shouldOpenBySwiping = in.readByte() != 0;
        shouldChangePaymentMethod = in.readByte() != 0;
        shouldVerifyEmail = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(barId);
        dest.writeDouble(distanceKm);
        dest.writeString(nameValue);
        dest.writeString(type);
        dest.writeString(discount);
        dest.writeString(image);
        dest.writeString(distance);
        dest.writeString(swipingText);
        dest.writeString(barWorkType);
        dest.writeByte((byte) (shouldAutoOpenTab ? 1 : 0));
        dest.writeByte((byte) (shouldOpenBySwiping ? 1 : 0));
        dest.writeByte((byte) (shouldChangePaymentMethod ? 1 : 0));
        dest.writeByte((byte) (shouldVerifyEmail ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InitialVenueDetailModel> CREATOR = new Creator<InitialVenueDetailModel>() {
        @Override
        public InitialVenueDetailModel createFromParcel(Parcel in) {
            return new InitialVenueDetailModel(in);
        }

        @Override
        public InitialVenueDetailModel[] newArray(int size) {
            return new InitialVenueDetailModel[size];
        }
    };

    public long getBarId() {
        return barId;
    }

    public void setBarId(long barId) {
        this.barId = barId;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getNameValue() {
        return nameValue;
    }

    public void setNameValue(String nameValue) {
        this.nameValue = nameValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSwipingText() {
        return swipingText;
    }

    public void setSwipingText(String swipingText) {
        this.swipingText = swipingText;
    }

    public String getBarWorkType() {
        return barWorkType;
    }

    public void setBarWorkType(String barWorkType) {
        this.barWorkType = barWorkType;
    }

    public boolean isShouldOpenBySwiping() {
        return shouldOpenBySwiping;
    }

    public void setShouldOpenBySwiping(boolean shouldOpenBySwiping) {
        this.shouldOpenBySwiping = shouldOpenBySwiping;
    }

    public boolean isShouldAutoOpenTab() {
        return shouldAutoOpenTab;
    }

    public void setShouldAutoOpenTab(boolean shouldAutoOpenTab) {
        this.shouldAutoOpenTab = shouldAutoOpenTab;
    }

    public boolean isShouldChangePaymentMethod() {
        return shouldChangePaymentMethod;
    }

    public void setShouldChangePaymentMethod(boolean shouldChangePaymentMethod) {
        this.shouldChangePaymentMethod = shouldChangePaymentMethod;
    }

    public boolean isShouldVerifyEmail() {
        return shouldVerifyEmail;
    }

    public void setShouldVerifyEmail(boolean shouldVerifyEmail) {
        this.shouldVerifyEmail = shouldVerifyEmail;
    }
}
