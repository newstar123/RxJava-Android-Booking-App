package app.gateway.checkin.tip;

import app.core.checkin.tip.entity.ConfirmTipsRequest;
import app.core.checkin.tip.gateway.PutConfirmTips;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutConfirmTipsRestGateway implements PutConfirmTips {
    private final PutGratuityGateway gratuityGateway;
    private final PutExactGratuityGateway exactGratuityGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public PutConfirmTipsRestGateway(BaseActivity activity) {
        gratuityGateway = QorumHttpClient.get().create(PutGratuityGateway.class);
        exactGratuityGateway = QorumHttpClient.get().create(PutExactGratuityGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<CheckInResponse> put(ConfirmTipsRequest request) {
        return putToken(request);
    }

    private Observable<CheckInResponse> putToken(ConfirmTipsRequest request) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> putId(token, request));
    }

    private Observable<CheckInResponse> putId(Token token, ConfirmTipsRequest request) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> putBody(tokenWithPrefix, userId, request));
    }

    private Observable<CheckInResponse> putBody(String tokenWithPrefix, Long userId, ConfirmTipsRequest request) {
        if (request.getExactGratuity() != null)
            return exactGratuityGateway.put(tokenWithPrefix, userId, request.getCheckInID(), request.getExactGratuity());
        else
            return gratuityGateway.put(tokenWithPrefix, userId, request.getCheckInID(), request.getGratuity());
    }
}
