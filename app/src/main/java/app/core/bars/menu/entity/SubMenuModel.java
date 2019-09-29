package app.core.bars.menu.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SubMenuModel implements Parcelable{
    private String id;
    private String name;
    private double price;

    public SubMenuModel(){}

    private SubMenuModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readDouble();
    }

    public static final Creator<SubMenuModel> CREATOR = new Creator<SubMenuModel>() {
        @Override
        public SubMenuModel createFromParcel(Parcel in) {
            return new SubMenuModel(in);
        }

        @Override
        public SubMenuModel[] newArray(int size) {
            return new SubMenuModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeDouble(price);
    }
}
