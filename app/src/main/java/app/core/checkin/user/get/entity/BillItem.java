package app.core.checkin.user.get.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;


public class BillItem implements Serializable {
    private int quantity;
    @SerializedName("omnivore_ticket_item_id") private String omnivoreTicketItemId;
    private String description;
    @SerializedName("updated_at") private Date updateDate;
    private String name;
    @SerializedName("checkin_id") private int checkinId;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("price_per_unit") private double pricePerUnit;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOmnivoreTicketItemId() {
        return omnivoreTicketItemId;
    }

    public void setOmnivoreTicketItemId(String omnivoreTicketItemId) {
        this.omnivoreTicketItemId = omnivoreTicketItemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
