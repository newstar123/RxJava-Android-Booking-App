package app.delivering.mvp.bars.detail.init.friends.init.model;

import android.os.Parcel;
import android.os.Parcelable;

import app.core.checkin.friends.entity.CheckinsFriendModel;

public class CheckinsPersonModel implements Parcelable {
    private CheckInsPeopleType type;
    private CheckinsFriendModel person;

    public CheckinsPersonModel(){}

    protected CheckinsPersonModel(Parcel in) {
        person = in.readParcelable(CheckinsFriendModel.class.getClassLoader());
        type = CheckInsPeopleType.valueOf(in.readString());
    }

    public static final Creator<CheckinsPersonModel> CREATOR = new Creator<CheckinsPersonModel>() {
        @Override
        public CheckinsPersonModel createFromParcel(Parcel in) {
            return new CheckinsPersonModel(in);
        }

        @Override
        public CheckinsPersonModel[] newArray(int size) {
            return new CheckinsPersonModel[size];
        }
    };

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(person, flags);
        dest.writeString(type.name());
    }

    public CheckInsPeopleType getType() {
        return type;
    }

    public void setType(CheckInsPeopleType type) {
        this.type = type;
    }

    public CheckinsFriendModel getPerson() {
        return person;
    }

    public void setPerson(CheckinsFriendModel person) {
        this.person = person;
    }
}
