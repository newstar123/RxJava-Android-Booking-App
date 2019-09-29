package app.gateway.facebook.friends.cache.get;

import java.util.List;

import app.core.facebook.friends.entity.FacebookFriendModel;
import app.gateway.facebook.friends.cache.holder.FacebookFriendsRealTimeHolder;
import rx.Observable;

public class GetFacebookFriendsRealTimeGateway implements GetFriendsInRealTimeGateway {

    @Override public Observable<List<FacebookFriendModel>> get() {
        return FacebookFriendsRealTimeHolder.getList();
    }
}
