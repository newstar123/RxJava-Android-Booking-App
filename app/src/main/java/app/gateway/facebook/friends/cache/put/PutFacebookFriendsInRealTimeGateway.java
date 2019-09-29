package app.gateway.facebook.friends.cache.put;

import java.util.List;

import app.core.facebook.friends.entity.FacebookFriendModel;
import app.gateway.facebook.friends.cache.holder.FacebookFriendsRealTimeHolder;
import rx.Observable;

public class PutFacebookFriendsInRealTimeGateway implements PutFriendsInRealTimeGateway{
    @Override public Observable<List<FacebookFriendModel>> put(List<FacebookFriendModel> list) {
        return FacebookFriendsRealTimeHolder.setList(list);
    }
}
