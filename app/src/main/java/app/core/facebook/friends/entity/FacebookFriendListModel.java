package app.core.facebook.friends.entity;

import java.util.List;

public class FacebookFriendListModel {
    private List<FacebookFriendModel> friendModels;

    public List<FacebookFriendModel> getFriendModels() {
        return friendModels;
    }

    public void setFriendModels(List<FacebookFriendModel> friendModels) {
        this.friendModels = friendModels;
    }
}
