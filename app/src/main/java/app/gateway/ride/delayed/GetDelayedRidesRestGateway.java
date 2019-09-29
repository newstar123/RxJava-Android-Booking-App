package app.gateway.ride.delayed;

import android.content.Context;

import java.util.List;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.init.token.entity.Token;
import app.core.ride.delayed.gateway.GetDelayedRidesGateway;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetDelayedRidesRestGateway implements GetDelayedRidesGateway {
    private final AuthTokenWithContextGateway androidAuthTokenGateway;
    private final GetDelayedRidesRetrofitGateway checkRidePromoRetrofitGateway;

    public GetDelayedRidesRestGateway(Context context) {
        checkRidePromoRetrofitGateway = QorumHttpClient.get().create(GetDelayedRidesRetrofitGateway.class);
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
    }

    @Override public Observable<List<GetCheckInsResponse>> get() {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(this::doRequest);
    }

    private Observable<List<GetCheckInsResponse>> doRequest(Token token) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> checkRidePromoRetrofitGateway.get(tokenWithPrefix, userId));
    }
}
