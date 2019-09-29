package app.gateway.facebook.friends.cache.get;

import java.util.List;

import app.core.facebook.friends.entity.FacebookFriendModel;
import rx.Observable;

public interface GetFriendsInRealTimeGateway {
    Observable<List<FacebookFriendModel>> get();
}
