package app.core.location.blocker.entity;

import com.google.gson.annotations.SerializedName;

public class LocationsDemandResponse {

    @SerializedName("email") private String email;
    @SerializedName("longitude") private double longitude;
    @SerializedName("latitude") private double latitude;
    @SerializedName("radius") private double radius;
    @SerializedName("created_at") private String createdAt;
    @SerializedName("updated_at") private String updatedAt;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
