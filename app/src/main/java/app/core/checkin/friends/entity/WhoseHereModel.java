package app.core.checkin.friends.entity;

import java.util.List;

import app.delivering.mvp.bars.detail.init.friends.init.model.CheckinsPersonModel;

public class WhoseHereModel {
    private boolean isAuthorized;
    private boolean isFBVisible;
    private List<CheckinsPersonModel> people;
    private List<CheckinsFriendModel> checkIns;
    private List<CheckinsFriendModel> friends;

    public WhoseHereModel() {}

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public void setFriends(List<CheckinsFriendModel> friends) {
        this.friends = friends;
    }

    public List<CheckinsFriendModel> getFriends() {
        return friends;
    }

    public List<CheckinsFriendModel> getCheckIns() {
        return checkIns;
    }

    public void setCheckIns(List<CheckinsFriendModel> checkIns) {
        this.checkIns = checkIns;
    }

    public List<CheckinsPersonModel> getPeople() {
        return people;
    }

    public void setPeople(List<CheckinsPersonModel> people) {
        this.people = people;
    }

    public boolean isFBVisible() {
        return isFBVisible;
    }

    public void setFBVisible(boolean FBVisible) {
        isFBVisible = FBVisible;
    }
}
