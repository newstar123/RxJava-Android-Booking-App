package app.gateway.checkin.recent;


import java.util.List;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.get.gateway.GetCheckInsGateway;
import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetCheckInsRestGateway implements GetCheckInsGateway {
    private final AndroidAuthTokenGateway androidAuthTokenGateway;
    private GetCheckInRetrofitGateway getCheckInRetrofitGateway;

    public GetCheckInsRestGateway(BaseActivity activity) {
        getCheckInRetrofitGateway = QorumHttpClient.get().create(GetCheckInRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<List<GetCheckInsResponse>> get(int recent) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> getCheckIns(token, recent))
                .compose(Rx401Policy.apply());
    }

    private Observable<List<GetCheckInsResponse>> getCheckIns(Token token, int recent) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> getCheckInRetrofitGateway.post(tokenWithPrefix, userId, recent));
    }
}
