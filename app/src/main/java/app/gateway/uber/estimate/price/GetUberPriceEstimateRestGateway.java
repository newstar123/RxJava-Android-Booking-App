package app.gateway.uber.estimate.price;

import com.google.android.gms.maps.model.LatLng;

import app.core.init.token.entity.Token;
import app.core.uber.estimate.price.entity.UberPriceEstimatesResponse;
import app.core.uber.estimate.price.gateway.GetUberPriceEstimatesGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetUberPriceEstimateRestGateway implements GetUberPriceEstimatesGateway {
    private final GetUberPriceEstimatesRetrofitGateway gateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public GetUberPriceEstimateRestGateway(BaseActivity activity) {
        gateway = QorumHttpClient.get().create(GetUberPriceEstimatesRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<UberPriceEstimatesResponse> get(LatLng start, LatLng stop) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> getEstimate(token, start, stop));
    }

    private Observable<UberPriceEstimatesResponse> getEstimate(Token token, LatLng start, LatLng stop) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> gateway.get(tokenWithPrefix, userId, start.latitude, start.longitude, stop.latitude,  stop.longitude));
    }
}
