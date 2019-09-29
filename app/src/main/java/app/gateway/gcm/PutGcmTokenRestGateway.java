package app.gateway.gcm;

import android.content.Context;

import app.core.gcm.entity.GcmTokenRequest;
import app.core.gcm.gateway.PutGcmToken;
import app.core.init.token.entity.Token;
import app.core.payment.regular.model.EmptyResponse;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutGcmTokenRestGateway implements PutGcmToken {
    private static final String PLATFORM = "android";
    private final PutGcmTokenGateway gateway;
    private final AuthTokenWithContextGateway androidAuthTokenGateway;

    public PutGcmTokenRestGateway(Context context) {
        gateway = QorumHttpClient.get().create(PutGcmTokenGateway.class);
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
    }

    @Override public Observable<EmptyResponse> put(String newToken) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> putWithToken(token, newToken));
    }

    private Observable<EmptyResponse> putWithToken(Token token, String newToken) {
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> putWithTokenAndUser(token, userId, newToken));
    }

    private Observable<EmptyResponse> putWithTokenAndUser(Token token, Long userId, String newToken) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return getRequestBody(newToken).concatMap(request -> gateway.put(tokenWithPrefix, userId, request));
    }

    private Observable<GcmTokenRequest> getRequestBody(String newToken) {
        return Observable.just(newToken)
                .map(s -> {
                    GcmTokenRequest request = new GcmTokenRequest();
                    request.setMobileId(newToken);
                    request.setMobilePlatform(PLATFORM);
                    return request;
                });
    }
}
