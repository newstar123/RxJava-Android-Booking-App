package app.core.uber.mock.ride.entity;


import com.google.gson.annotations.SerializedName;

public class UberReceiptResponse {
    private String subtotal;
    @SerializedName("total_charged") private String totalCharged;
    @SerializedName("total_fare") private String totalFare;

    public String getSubtotal() {
        return subtotal;
    }

    public String getTotalCharged() {
        return totalCharged;
    }

    public String getTotalFare() {
        return totalFare;
    }
}
