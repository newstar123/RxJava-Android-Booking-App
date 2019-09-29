package app.gateway.facebook.friends.get;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.core.facebook.friends.entity.FacebookFriendModel;
import app.core.login.facebook.entity.LoginResponse;
import app.delivering.component.BaseActivity;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.facebook.friends.cache.get.GetFacebookFriendsRealTimeGateway;
import app.gateway.facebook.friends.cache.put.PutFacebookFriendsInRealTimeGateway;
import app.gateway.facebook.friends.exceptions.EmptyRealTimeFacebookFriendsException;
import app.gateway.facebook.friends.graph.FacebookGraphFriendsGateway;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;

public class GetFacebookFriendsGateway implements GetFriendsGateway {
    private final FacebookGraphFriendsGateway graphFriendsGateway;
    private final GetFacebookFriendsRealTimeGateway getFriendsRealTimeGateway;
    private final PutFacebookFriendsInRealTimeGateway putFriendsInRealTimeGateway;
    private final AuthTokenWithContextGateway tokenWithContextGateway;

    public GetFacebookFriendsGateway(BaseActivity context) {
        graphFriendsGateway = new FacebookGraphFriendsGateway();
        getFriendsRealTimeGateway = new GetFacebookFriendsRealTimeGateway();
        putFriendsInRealTimeGateway = new PutFacebookFriendsInRealTimeGateway();
        tokenWithContextGateway = new AuthTokenWithContextGateway(context);
    }

    @Override
    public Observable<List<FacebookFriendModel>> get() {
        return getFriendsRealTimeGateway.get()
                .doOnNext(barModels -> {
                    if (barModels == null || barModels.isEmpty()) {
                        Log.d("GetFriendsGateway", "listRealTimeGateway");
                        throw Exceptions.propagate(new EmptyRealTimeFacebookFriendsException());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext(throwable -> {
                    if (throwable.getCause() instanceof EmptyRealTimeFacebookFriendsException) {
                        Log.d("GetFriendsGateway", "listRestGateway");
                        return getFriendsFromGraphAndSaveToRealtime();
                    } else
                        return Observable.error(throwable.getCause());
                });
    }

    private Observable<List<FacebookFriendModel>> getFriendsFromGraphAndSaveToRealtime() {
        return tokenWithContextGateway.get()
                .concatMap(token -> {
                    if (token.getAuthToken().equals(LoginResponse.GUEST_TOKEN))
                        return Observable.just(new ArrayList<>());
                    else
                        return graphFriendsGateway.get()
                                .concatMap(listModel -> putFriendsInRealTimeGateway.put(listModel.getFriendModels()));
                });
    }
}
