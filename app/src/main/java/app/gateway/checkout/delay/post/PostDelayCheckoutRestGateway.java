package app.gateway.checkout.delay.post;

import android.content.Context;
import android.provider.ContactsContract;

import app.core.checkout.delay.DelayCheckoutInteractor;
import app.core.checkout.gateway.PutCheckoutGateway;
import app.core.init.token.entity.Token;
import app.core.payment.regular.model.EmptyResponse;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PostDelayCheckoutRestGateway implements PutCheckoutGateway {
    private final AuthTokenWithContextGateway androidAuthTokenGateway;
    private PostDelayCheckoutGateway gateway;

    public PostDelayCheckoutRestGateway(Context context) {
        gateway = QorumHttpClient.get().create(PostDelayCheckoutGateway.class);
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
    }

    @Override public Observable<EmptyResponse> put(long checkInId) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> checkOut(token, checkInId))
                .flatMap(dataResponse -> {
                    if (dataResponse.code() == 200 || dataResponse.code() == 204)
                        return Observable.just(new EmptyResponse());
                    throw new RuntimeException();
                });
    }

    private Observable<Response<ContactsContract.Data>> checkOut(Token token, long checkInId) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long)QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> gateway.put(tokenWithPrefix, userId, checkInId, DelayCheckoutInteractor.DELAY, new EmptyResponse()));
    }
}
