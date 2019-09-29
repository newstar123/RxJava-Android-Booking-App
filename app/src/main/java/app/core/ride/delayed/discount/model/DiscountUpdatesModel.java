package app.core.ride.delayed.discount.model;

import app.core.advert.entity.AdvertResponse;

public class DiscountUpdatesModel {
    private AdvertResponse advertResponse;
    private double rideDiscountVal;
    private boolean isEligible;

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }

    public AdvertResponse getAdvertResponse() {
        return advertResponse;
    }

    public void setAdvertResponse(AdvertResponse advertResponse) {
        this.advertResponse = advertResponse;
    }

    public double getRideDiscountVal() {
        return rideDiscountVal;
    }

    public void setRideDiscountVal(double rideDiscount) {
        rideDiscountVal = rideDiscount;
    }
}
