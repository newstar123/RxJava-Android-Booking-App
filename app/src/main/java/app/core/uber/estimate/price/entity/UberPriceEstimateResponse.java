package app.core.uber.estimate.price.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UberPriceEstimateResponse implements Serializable{
    @SerializedName("localized_display_name") private String localizedDisplayName;
    @SerializedName("distance") private double distance;
    @SerializedName("display_name") private String displayName;
    @SerializedName("product_id") private String productId;
    @SerializedName("high_estimate") private double highEstimate;
    @SerializedName("surge_multiplier") private int surgeMultiplier;
    @SerializedName("minimum") private double minimum;
    @SerializedName("low_estimate") private double lowEstimate;
    @SerializedName("duration") private int duration;
    @SerializedName("estimate") private String summeryEstimate;
    @SerializedName("currency_code") private String currencyCode;

    public String getLocalizedDisplayName() {
        return localizedDisplayName;
    }

    public double getDistance() {
        return distance;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProductId() {
        return productId;
    }

    public double getHighEstimate() {
        return highEstimate;
    }

    public int getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getLowEstimate() {
        return lowEstimate;
    }

    public int getDuration() {
        return duration;
    }

    public String getSummeryEstimate() {
        return summeryEstimate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

}
