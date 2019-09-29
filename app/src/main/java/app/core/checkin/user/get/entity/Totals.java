package app.core.checkin.user.get.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Totals implements Serializable {
    private int id;
    private double due;
    private double tax;
    @SerializedName("approximateTax") private double approximateTax;
    private double total;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("updated_at") private Date updatedAt;
    @SerializedName("other_charges") private double otherCharges;
    @SerializedName("service_charges") private double serviceCharges;
    @SerializedName("sub_total") private int subTotal;
    @SerializedName("undiscounted_sub_total") private double unDiscountedSubTotal;
    @SerializedName("promotion_discount") private double promotionDiscount;
    @SerializedName("checkin_id") private int checkinId;
    @SerializedName("freedrinks_discount") private double freeDrinksDiscount;

    public double getFreeDrinksDiscount() {
        return freeDrinksDiscount;
    }

    public void setFreeDrinksDiscount(double freeDrinksDiscount) {
        this.freeDrinksDiscount = freeDrinksDiscount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public double getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(double serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(int checkinId) {
        this.checkinId = checkinId;
    }

    public double getUnDiscountedSubTotal() {
        return unDiscountedSubTotal;
    }

    public void setUnDiscountedSubTotal(double unDiscountedSubTotal) {
        this.unDiscountedSubTotal = unDiscountedSubTotal;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(double promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    public double getApproximateTax() {
        return approximateTax;
    }

    public void setApproximateTax(double approximateTax) {
        this.approximateTax = approximateTax;
    }
}
