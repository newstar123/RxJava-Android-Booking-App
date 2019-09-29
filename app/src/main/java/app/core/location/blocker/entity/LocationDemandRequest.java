package app.core.location.blocker.entity;

import com.google.gson.annotations.SerializedName;

public class LocationDemandRequest {

    @SerializedName("email") private String email;
    @SerializedName("longitude") private double longitude;
    @SerializedName("latitude") private double latitude;
    @SerializedName("radius") private double radius;

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
}
