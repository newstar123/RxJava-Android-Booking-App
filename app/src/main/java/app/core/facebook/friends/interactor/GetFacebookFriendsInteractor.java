package app.core.facebook.friends.interactor;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.facebook.friends.entity.FacebookFriendModel;
import app.delivering.component.BaseActivity;
import app.gateway.facebook.friends.get.GetFacebookFriendsGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetFacebookFriendsInteractor implements BaseOutputInteractor<Observable<List<FacebookFriendModel>>> {
    private GetFacebookFriendsGateway friendsGateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;

    public GetFacebookFriendsInteractor(BaseActivity activity){
        friendsGateway = new GetFacebookFriendsGateway(activity);
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
    }

    @Override public Observable<List<FacebookFriendModel>> process() {
        return networkPermissionGateway.check()
                .concatMap(isOk -> friendsGateway.get());
    }
}
