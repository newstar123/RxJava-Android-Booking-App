package app.gateway.checkin.item;

import android.content.Context;

import app.core.checkin.item.gateway.GetCheckInGateway;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.init.token.entity.Token;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.qamode.log.LogToFileHandler;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetCheckInByIdRestGateway implements GetCheckInGateway {
    private final GetCheckInByIdGateway gateway;
    private final AuthTokenWithContextGateway androidAuthTokenGateway;

    public GetCheckInByIdRestGateway(Context context) {
        gateway = QorumHttpClient.get().create(GetCheckInByIdGateway.class);
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
    }

    @Override public Observable<CheckInResponse> get(long checkInId, boolean isForceRequest) {
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - get check-in-" + checkInId);
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> getCheckin(token, checkInId, isForceRequest));
    }

    private Observable<CheckInResponse> getCheckin(Token token, long checkInId, boolean isForceRequest) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
        .concatMap(userId -> gateway.get(tokenWithPrefix, userId, checkInId, isForceRequest));
    }
}
