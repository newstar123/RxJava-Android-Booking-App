package app.core.bars.locations.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class LocationsModel implements Serializable{
    private Integer id;
    private String label;
    private double latitude;
    private double longitude;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("ridesafe_rounds_min")
    private int ridesafeRoundsMin;
    @SerializedName("ridesafe_spend_round_min")
    private int ridesafeSpendRoundMin;
    @SerializedName("ridesafe_discount_value")
    private int ridesafeDiscountValue;
    @SerializedName("is_active") private boolean isActive;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public int getRidesafeRoundsMin() {
        return ridesafeRoundsMin;
    }

    public void setRidesafeRoundsMin(int ridesafeRoundsMin) {
        this.ridesafeRoundsMin = ridesafeRoundsMin;
    }

    public int getRidesafeSpendRoundMin() {
        return ridesafeSpendRoundMin;
    }

    public void setRidesafeSpendRoundMin(int ridesafeSpendRoundMin) {
        this.ridesafeSpendRoundMin = ridesafeSpendRoundMin;
    }

    public int getRidesafeDiscountValue() {
        return ridesafeDiscountValue;
    }

    public void setRidesafeDiscountValue(int ridesafeDiscountValue) {
        this.ridesafeDiscountValue = ridesafeDiscountValue;
    }

    public boolean isActive() {
        return isActive;
    }
}
