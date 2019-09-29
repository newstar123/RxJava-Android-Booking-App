package app.core.bars.list.get.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeaconModel implements Serializable {
    private String id;
    @SerializedName("factory_id") private String factoryId;
    @SerializedName("icon_url") private String iconUrl;
    private String name;
    private double latitude;
    private double longitude;
    @SerializedName("gimbal_latitude") private double gimbalLatitude;
    @SerializedName("gimbal_longitude") private double gimbalLongitude;
    private String visibility;
    @SerializedName("beacon_attributes") private BeaconAttributeModel attributes;
    @SerializedName("beaconConfig") private BeaconConfigModel beaconConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getGimbalLatitude() {
        return gimbalLatitude;
    }

    public void setGimbalLatitude(double gimbalLatitude) {
        this.gimbalLatitude = gimbalLatitude;
    }

    public double getGimbalLongitude() {
        return gimbalLongitude;
    }

    public void setGimbalLongitude(double gimbalLongitude) {
        this.gimbalLongitude = gimbalLongitude;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public BeaconAttributeModel getAttributes() {
        return attributes;
    }

    public void setAttributes(BeaconAttributeModel attributes) {
        this.attributes = attributes;
    }

    public BeaconConfigModel getBeaconConfig() {
        return beaconConfig;
    }

    public void setBeaconConfig(BeaconConfigModel beaconConfig) {
        this.beaconConfig = beaconConfig;
    }
}