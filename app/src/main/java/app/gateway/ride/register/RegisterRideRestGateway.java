package app.gateway.ride.register;

import app.core.init.token.entity.Token;
import app.core.uber.start.entity.RegisterRideRequest;
import app.core.uber.start.entity.RegisterRideResponse;
import app.core.uber.start.gateway.RegisterRideGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;


public class RegisterRideRestGateway implements RegisterRideGateway {
    private final RegisterRideRetrofitGateway registerRideRetrofitGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public RegisterRideRestGateway(BaseActivity activity) {
        registerRideRetrofitGateway = QorumHttpClient.get().create(RegisterRideRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<RegisterRideResponse> post(RegisterRideRequest request) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> doRequest(token, request))
                .compose(Rx401Policy.apply());
    }

    private Observable<RegisterRideResponse> doRequest(Token token, RegisterRideRequest request) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> registerRideRetrofitGateway.post(tokenWithPrefix, userId, request));
    }
}
