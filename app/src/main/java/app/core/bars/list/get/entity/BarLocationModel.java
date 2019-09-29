package app.core.bars.list.get.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BarLocationModel {
    private long id;
    private String label;
    private double latitude;
    private double longitude;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("updated_at") private Date updatedAt;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("ridesafe_rounds_min") private double ridesafeRoundsMin;
    @SerializedName("ridesafe_spend_round_min") private double ridesafeSpendRoundMin;
    @SerializedName("ridesafe_discount_value") private double ridesafeDiscountValue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRidesafeRoundsMin() {
        return ridesafeRoundsMin;
    }

    public void setRidesafeRoundsMin(double ridesafeRoundsMin) {
        this.ridesafeRoundsMin = ridesafeRoundsMin;
    }

    public double getRidesafeSpendRoundMin() {
        return ridesafeSpendRoundMin;
    }

    public void setRidesafeSpendRoundMin(double ridesafeSpendRoundMin) {
        this.ridesafeSpendRoundMin = ridesafeSpendRoundMin;
    }

    public double getRidesafeDiscountValue() {
        return ridesafeDiscountValue;
    }

    public void setRidesafeDiscountValue(double ridesafeDiscountValue) {
        this.ridesafeDiscountValue = ridesafeDiscountValue;
    }
}
