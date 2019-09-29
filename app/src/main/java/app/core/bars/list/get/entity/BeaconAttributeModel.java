package app.core.bars.list.get.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeaconAttributeModel implements Serializable {
    @SerializedName("venue_id") private long venueId;
    @SerializedName("checkin_radius") private int checkinRadius;
    @SerializedName("checkout_radius") private int checkoutRadius;
    @SerializedName("config_id") private String configId;

    public long getVenueId() {
        return venueId;
    }

    public void setVenueId(long venueId) {
        this.venueId = venueId;
    }

    public int getCheckinRadius() {
        return checkinRadius;
    }

    public void setCheckinRadius(int checkinRadius) {
        this.checkinRadius = checkinRadius;
    }

    public int getCheckoutRadius() {
        return checkoutRadius;
    }

    public void setCheckoutRadius(int checkoutRadius) {
        this.checkoutRadius = checkoutRadius;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }
}
