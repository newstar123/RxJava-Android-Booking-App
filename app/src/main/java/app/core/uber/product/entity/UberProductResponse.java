package app.core.uber.product.entity;


import com.google.gson.annotations.SerializedName;

public class UberProductResponse {
    @SerializedName("product_id") private String productId;
    @SerializedName("display_name") private String displayName;
    private String description;
    private int capacity;
    private boolean shared;
    @SerializedName("upfront_fare_enabled") private boolean upfrontFareEnabled;

    public String getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isUpfrontFareEnabled() {
        return upfrontFareEnabled;
    }

    public void setUpfrontFareEnabled(boolean upfrontFareEnabled) {
        this.upfrontFareEnabled = upfrontFareEnabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
