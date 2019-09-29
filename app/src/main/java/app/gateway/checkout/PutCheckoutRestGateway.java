package app.gateway.checkout;

import android.content.Context;

import app.core.checkout.gateway.PutCheckoutGateway;
import app.core.init.token.entity.Token;
import app.core.payment.regular.model.EmptyResponse;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutCheckoutRestGateway implements PutCheckoutGateway {
    private final AuthTokenWithContextGateway androidAuthTokenGateway;
    private PutCheckoutRetrofitGateway gateway;

    public PutCheckoutRestGateway(Context context) {
        gateway = QorumHttpClient.get().create(PutCheckoutRetrofitGateway.class);
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
    }

    @Override public Observable<EmptyResponse> put(long checkInId) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> checkOut(token, checkInId));
    }

    private Observable<EmptyResponse> checkOut(Token token, long checkInId) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> gateway.put(tokenWithPrefix, userId, checkInId, new EmptyResponse()));
    }
}
