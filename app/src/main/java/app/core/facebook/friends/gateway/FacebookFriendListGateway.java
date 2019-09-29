package app.core.facebook.friends.gateway;

import app.core.facebook.friends.entity.FacebookFriendListModel;
import rx.Observable;

public interface FacebookFriendListGateway {
    Observable<FacebookFriendListModel> get();
}
