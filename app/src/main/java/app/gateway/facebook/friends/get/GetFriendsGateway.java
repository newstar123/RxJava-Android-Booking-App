package app.gateway.facebook.friends.get;

import java.util.List;

import app.core.facebook.friends.entity.FacebookFriendModel;
import rx.Observable;

public interface GetFriendsGateway {
    Observable<List<FacebookFriendModel>> get();
}
