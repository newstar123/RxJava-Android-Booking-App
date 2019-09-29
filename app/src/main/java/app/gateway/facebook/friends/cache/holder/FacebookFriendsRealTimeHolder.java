package app.gateway.facebook.friends.cache.holder;

import java.util.List;

import app.core.facebook.friends.entity.FacebookFriendModel;
import rx.Observable;

public class FacebookFriendsRealTimeHolder {
    private static List<FacebookFriendModel> friends;
    private static final Object friendsLock = new Object();

    public static Observable<List<FacebookFriendModel>> setList(List<FacebookFriendModel> models){
        synchronized (friendsLock){
            FacebookFriendsRealTimeHolder.friends = models;
           return getList();
        }
    }

    public static Observable<List<FacebookFriendModel>> getList(){
        synchronized (friendsLock){
          return Observable.just(FacebookFriendsRealTimeHolder.friends);
        }
    }

    public static void clear(){
        synchronized (friendsLock){
            friends = null;
        }
    }
}
