package app.delivering.mvp.tab.discount.model;

import android.text.Spanned;

public class DiscountModel {
    private String timerValue;
    private Spanned timeTitle;
    private int round;
    private int minRounds;
    private double maxDiscount;
    private long time;
    private double freeRideValFromCache;

    private boolean isFreeRideAndNoDiscount;
    private boolean isNoFreeRideAndNoDiscount;
    private boolean isFreeRideAndDiscount;
    private boolean isDiscountAndNoFreeRide;


    public boolean isFreeRideAndNoDiscount() {
        return isFreeRideAndNoDiscount;
    }

    public void setFreeRideAndNoDiscount(boolean freeRideAndNoDiscount) {
        isFreeRideAndNoDiscount = freeRideAndNoDiscount;
    }

    public boolean isNoFreeRideAndNoDiscount() {
        return isNoFreeRideAndNoDiscount;
    }

    public void setNoFreeRideAndNoDiscount(boolean noFreeRideAndNoDiscount) {
        isNoFreeRideAndNoDiscount = noFreeRideAndNoDiscount;
    }

    public boolean isFreeRideAndDiscount() {
        return isFreeRideAndDiscount;
    }

    public void setFreeRideAndDiscount(boolean freeRideAndDiscount) {
        isFreeRideAndDiscount = freeRideAndDiscount;
    }

    public boolean isDiscountAndNoFreeRide() {
        return isDiscountAndNoFreeRide;
    }

    public void setDiscountAndNoFreeRide(boolean discountAndNoFreeRide) {
        isDiscountAndNoFreeRide = discountAndNoFreeRide;
    }

    public String getTimerValue() {
        return timerValue;
    }

    public void setTimerValue(String timerValue) {
        this.timerValue = timerValue;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getMinRounds() {
        return minRounds;
    }

    public void setMinRounds(int minRounds) {
        this.minRounds = minRounds;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Spanned getTimeTitle() {
        return timeTitle;
    }

    public void setTimeTitle(Spanned timeTitle) {
        this.timeTitle = timeTitle;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public double isFreeRideValFromCache() {
        return freeRideValFromCache;
    }

    public void setFreeRideValFromCache(double freeRideValFromCache) {
        this.freeRideValFromCache = freeRideValFromCache;
    }
}
