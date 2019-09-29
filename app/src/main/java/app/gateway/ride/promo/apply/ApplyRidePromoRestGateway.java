package app.gateway.ride.promo.apply;

import app.core.init.token.entity.Token;
import app.core.payment.regular.model.EmptyResponse;
import app.core.uber.start.entity.ApplyRidePromoRequest;
import app.core.uber.start.entity.ApplyRidePromoResponse;
import app.core.uber.start.gateway.ApplyRidePromoGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;


public class ApplyRidePromoRestGateway implements ApplyRidePromoGateway {
    private final AndroidAuthTokenGateway androidAuthTokenGateway;
    private final ApplyRidePromoRetrofitGateway applyRidePromoRetrofitGateway;

    public ApplyRidePromoRestGateway(BaseActivity activity) {
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
        applyRidePromoRetrofitGateway = QorumHttpClient.get().create(ApplyRidePromoRetrofitGateway.class);
    }

    @Override public Observable<ApplyRidePromoResponse> put(ApplyRidePromoRequest request) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> doRequest(token, request))
                .compose(Rx401Policy.apply());
    }

    private Observable<ApplyRidePromoResponse> doRequest(Token token, ApplyRidePromoRequest request) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> applyRidePromoRetrofitGateway.put(tokenWithPrefix,
                        userId,
                        request.getCheckinId(),
                        request.getRideId(),
                        new EmptyResponse()));
    }
}
