package app.gateway.checkin.user;

import app.core.checkin.user.post.entity.CheckInRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkin.user.post.gateway.PostCheckInGateway;
import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PostCheckInRestGateway implements PostCheckInGateway {
    private final PostCheckInRetrofitGateway gateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public PostCheckInRestGateway(BaseActivity activity) {
        gateway = QorumHttpClient.get().create(PostCheckInRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    private Observable<CheckInResponse> checkIn(Token token, CheckInRequest request) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> gateway.post(tokenWithPrefix,request , userId));
    }

    @Override public Observable<CheckInResponse> post(CheckInRequest request) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> checkIn(token, request))
                .compose(Rx401Policy.apply());
    }
}
