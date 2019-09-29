package app.core.bars.list.get.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeaconConfigModel implements Serializable {
    private long id;
    private String name;
    @SerializedName("beacon_type") private String beaconType;
    @SerializedName("proximity_uuid") private String proximityUuid;
    private int major;
    private int minor;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeaconType() {
        return beaconType;
    }

    public void setBeaconType(String beaconType) {
        this.beaconType = beaconType;
    }

    public String getProximityUuid() {
        return proximityUuid;
    }

    public void setProximityUuid(String proximityUuid) {
        this.proximityUuid = proximityUuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }
}
