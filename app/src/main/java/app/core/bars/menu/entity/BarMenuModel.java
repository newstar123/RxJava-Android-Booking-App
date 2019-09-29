package app.core.bars.menu.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class BarMenuModel implements Parcelable {
    private String id;
    private String name;
    private List<SubMenuModel> items;

    public BarMenuModel(){}

    private BarMenuModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        items = in.createTypedArrayList(SubMenuModel.CREATOR);
    }

    public static final Creator<BarMenuModel> CREATOR = new Creator<BarMenuModel>() {
        @Override
        public BarMenuModel createFromParcel(Parcel in) {
            return new BarMenuModel(in);
        }

        @Override
        public BarMenuModel[] newArray(int size) {
            return new BarMenuModel[size];
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

    public List<SubMenuModel> getItems() {
        return items;
    }

    public void setItems(List<SubMenuModel> items) {
        this.items = items;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeTypedList(items);
    }
}
