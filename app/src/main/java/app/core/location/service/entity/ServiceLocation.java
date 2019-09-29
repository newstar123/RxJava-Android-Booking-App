
package app.core.location.service.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class ServiceLocation implements Serializable {
    private Integer id;
    private String label;
    private double latitude;
    private double longitude;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("updated_at") private Date updatedAt;
    @SerializedName("image_url") private String imageUrl;
    @SerializedName("ridesafe_rounds_min") private int ridesafeRoundsMin;
    @SerializedName("ridesafe_spend_round_min") private int ridesafeSpendRoundMin;
    @SerializedName("ridesafe_discount_value") private int ridesafeDiscountValue;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
