package app.delivering.mvp.ride.order.fare.apply.model;


import android.support.annotation.DrawableRes;

public class RideType {
    private String productId;
    private String fare;
    private String name;
    @DrawableRes private int imageId;
    private String capacity;
    private String durationEstimate;
    private String pickupEstimate;
    private String slogan;
    private String fareId;
    private boolean share;
    private boolean upfrontFareEnabled;
    private String farePlusDiscountValue;
    private Long fareExpired;

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(@DrawableRes int imageId) {
        this.imageId = imageId;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDurationEstimate() {
        return durationEstimate;
    }

    public void setDurationEstimate(String durationEstimate) {
        this.durationEstimate = durationEstimate;
    }

    public String getPickupEstimate() {
        return pickupEstimate;
    }

    public void setPickupEstimate(String pickupEstimate) {
        this.pickupEstimate = pickupEstimate;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setFareId(String fareId) {
        this.fareId = fareId;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public boolean isUpfrontFareEnabled() {
        return upfrontFareEnabled;
    }

    public void setUpfrontFareEnabled(boolean upfrontFareEnabled) {
        this.upfrontFareEnabled = upfrontFareEnabled;
    }

    public String getFareId() {
        return fareId;
    }

    public boolean isShare() {
        return share;
    }

    public void setFarePlusDiscountValue(String farePlusDiscountValue) {
        this.farePlusDiscountValue = farePlusDiscountValue;
    }

    public String getFarePlusDiscountValue() {
        return farePlusDiscountValue;
    }

    public void setFareExpired(Long fareExpired) {
        this.fareExpired = fareExpired;
    }

    public Long getFareExpiredAt() {
        return fareExpired;
    }

}
