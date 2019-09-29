package app.gateway.ride.token.register;

import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;


public class RegisterRideAuthRestGateway implements RegisterRideAuthGateway{
    private final AndroidAuthTokenGateway androidAuthTokenGateway;
    private final RegisterRideAuthRetrofitGateway registerRideAuthRetrofitGateway;

    public RegisterRideAuthRestGateway(BaseActivity activity) {
        registerRideAuthRetrofitGateway = QorumHttpClient.get().create(RegisterRideAuthRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    private Observable<RegisterRideAuthResponse> doRequest(Token token, RegisterRideAuthRequest request) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> registerRideAuthRetrofitGateway.post(tokenWithPrefix, userId, request));
    }

    @Override public Observable<RegisterRideAuthResponse> post(RegisterRideAuthRequest request) {
            return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> doRequest(token, request))
                .compose(Rx401Policy.apply());
    }
}
