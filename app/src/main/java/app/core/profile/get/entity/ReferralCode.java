package app.core.profile.get.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class ReferralCode implements Serializable {
    private Integer id;
    @SerializedName("created_at")    private Date createdAt;
    @SerializedName("updated_at")    private Date updatedAt;
    private String code;
    private String type;
    private boolean active;
    @SerializedName("free_drinks")    private String freeDrinks;
    @SerializedName("patron_id")    private String patronId;
    @SerializedName("vendor_id")    private String vendorId;
    @SerializedName("branch_link")    private String branchLink;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFreeDrinks() {
        return freeDrinks;
    }

    public void setFreeDrinks(String freeDrinks) {
        this.freeDrinks = freeDrinks;
    }

    public String getPatronId() {
        return patronId;
    }

    public void setPatronId(String patronId) {
        this.patronId = patronId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getBranchLink() {
        return branchLink;
    }

    public void setBranchLink(String branchLink) {
        this.branchLink = branchLink;
    }
}
