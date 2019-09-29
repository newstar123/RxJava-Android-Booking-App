package app.gateway.facebook.friends.cache.put;

import java.util.List;

import app.core.facebook.friends.entity.FacebookFriendModel;
import rx.Observable;

public interface PutFriendsInRealTimeGateway {
    Observable<List<FacebookFriendModel>> put(List<FacebookFriendModel> list);
}
