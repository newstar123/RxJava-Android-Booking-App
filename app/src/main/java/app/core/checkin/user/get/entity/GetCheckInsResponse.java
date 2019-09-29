package app.core.checkin.user.get.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import app.core.profile.get.entity.ProfileModel;

public class GetCheckInsResponse implements Serializable, Parcelable {
    private int id;
    @SerializedName("checkout_time") private Date checkoutTime;
    private Totals totals;
    @SerializedName("created_at") private Date createdAt;
    @SerializedName("billItems") private List<BillItem> billItems;
    private int discount;
    private int gratuity;
    @SerializedName("exact_gratuity") private double exactGratuity;
    private ProfileModel patron;
    private String feedback;
    @SerializedName("num_drinks") private int numDrinks;
    @SerializedName("patron_id") private int patronId;
    private Integer rating;
    @SerializedName("updated_at") private Date updatedAt;
    @SerializedName("vendor_id") private long vendorId;
    @SerializedName("ridesafeDiscountStatus") private RideSafeDiscountStatus ridesafeDiscountStatus;
    @SerializedName("rideDiscount") private RideDiscount rideDiscount;
    @SerializedName("auto_closed")
    private boolean isAutoClosed;
    private BarModel vendor;

    public GetCheckInsResponse(){}

    protected GetCheckInsResponse(Parcel in) {
        id = in.readInt();
        discount = in.readInt();
        gratuity = in.readInt();
        exactGratuity = in.readDouble();
        feedback = in.readString();
        numDrinks = in.readInt();
        patronId = in.readInt();
        vendorId = in.readLong();
        vendor = in.readParcelable(BarModel.class.getClassLoader());
    }

    public static final Creator<GetCheckInsResponse> CREATOR = new Creator<GetCheckInsResponse>() {
        @Override
        public GetCheckInsResponse createFromParcel(Parcel in) {
            return new GetCheckInsResponse(in);
        }

        @Override
        public GetCheckInsResponse[] newArray(int size) {
            return new GetCheckInsResponse[size];
        }
    };

    public int getGratuity() {
        return gratuity;
    }

    public void setGratuity(int gratuity) {
        this.gratuity = gratuity;
    }

    public double getExactGratuity() {
        return exactGratuity;
    }

    public void setExactGratuity(double exactGratuity) {
        this.exactGratuity = exactGratuity;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }


    public List<BillItem> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
    }


    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getNumDrinks() {
        return numDrinks;
    }

    public void setNumDrinks(int numDrinks) {
        this.numDrinks = numDrinks;
    }


    public int getPatronId() {
        return patronId;
    }

    public void setPatronId(int patronId) {
        this.patronId = patronId;
    }


    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }


    public Date getUpdatedAt() {
        return updatedAt;
    }


    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setTotals(Totals totals) {
        this.totals = totals;
    }

    public Totals getTotals() {
        return totals;
    }

    public Date getCheckoutTime() {
        return checkoutTime;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(discount);
        parcel.writeInt(gratuity);
        parcel.writeDouble(exactGratuity);
        parcel.writeString(feedback);
        parcel.writeInt(numDrinks);
        parcel.writeInt(patronId);
        parcel.writeLong(vendorId);
        parcel.writeParcelable(vendor, i);
    }

    public ProfileModel getPatron() {
        return patron;
    }

    public void setPatron(ProfileModel patron) {
        this.patron = patron;
    }

    public BarModel getVendor() {
        return vendor;
    }

    public void setVendor(BarModel vendor) {
        this.vendor = vendor;
    }

    public RideDiscount getRideDiscount() {
        return rideDiscount;
    }

    public void setRideDiscount(RideDiscount rideDiscount) {
        this.rideDiscount = rideDiscount;
    }

    public RideSafeDiscountStatus getRideSafeDiscountStatus() {
        return ridesafeDiscountStatus;
    }

    public void setRidesafeDiscountStatus(RideSafeDiscountStatus ridesafeDiscountStatus) {
        this.ridesafeDiscountStatus = ridesafeDiscountStatus;
    }

    public boolean isAutoClosed() {
        return isAutoClosed;
    }

    public void setAutoClosed(boolean autoClosed) {
        isAutoClosed = autoClosed;
    }
}
