package app.core.checkin.user.post.entity;


import com.google.gson.annotations.SerializedName;

public class CheckInRequest {
    @SerializedName("vendor_id") long vendorId;

    public CheckInRequest(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }
}
