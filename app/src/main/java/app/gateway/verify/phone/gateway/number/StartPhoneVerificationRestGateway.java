package app.gateway.verify.phone.gateway.number;

import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import app.gateway.verify.phone.entity.number.StartPhoneRequestVerification;
import app.gateway.verify.phone.entity.number.StartPhoneResponseVerification;
import rx.Observable;
import rx.schedulers.Schedulers;

public class StartPhoneVerificationRestGateway implements StartPhoneVerificationGateway {
    private final StartPhoneVerificationRetrofitGateway startPhoneVerificationRetrofitGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public StartPhoneVerificationRestGateway(BaseActivity baseActivity) {
        startPhoneVerificationRetrofitGateway = QorumHttpClient.get().create(StartPhoneVerificationRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(baseActivity);
    }

    @Override
    public Observable<StartPhoneResponseVerification> verify(StartPhoneRequestVerification startPhoneRequestVerification) {
        return androidAuthTokenGateway.get()
                .concatMap(token -> addVerification(token, startPhoneRequestVerification))
                .compose(Rx401Policy.apply());
    }

    private Observable<StartPhoneResponseVerification> addVerification(Token token, StartPhoneRequestVerification startPhoneRequestVerification) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .observeOn(Schedulers.io())
                .concatMap(userId -> startPhoneVerificationRetrofitGateway.post(tokenWithPrefix, userId, startPhoneRequestVerification));
    }

}
