package app.gateway.ride.promo.check;

import app.core.init.token.entity.Token;
import app.core.uber.start.entity.CheckRidePromoRequest;
import app.core.uber.start.entity.CheckRidePromoResponse;
import app.core.uber.start.gateway.CheckRidePromoGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;


public class CheckRidePromoRestGateway implements CheckRidePromoGateway {
    private final AndroidAuthTokenGateway androidAuthTokenGateway;
    private final CheckRidePromoRetrofitGateway checkRidePromoRetrofitGateway;

    public CheckRidePromoRestGateway(BaseActivity activity) {
        checkRidePromoRetrofitGateway = QorumHttpClient.get().create(CheckRidePromoRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<CheckRidePromoResponse> get(CheckRidePromoRequest request) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> doRequest(token, request))
                .compose(Rx401Policy.apply());
    }

    private Observable<CheckRidePromoResponse> doRequest(Token token, CheckRidePromoRequest request) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> checkRidePromoRetrofitGateway.get(tokenWithPrefix, userId, request.getCheckinId()));
    }
}
