package app.gateway.verify.phone.gateway.code;

import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.twilio.TwilioRx401Policy;
import app.gateway.verify.phone.entity.code.CheckPhoneRequestVerification;
import app.gateway.verify.phone.entity.code.CheckPhoneResponseVerification;
import rx.Observable;
import rx.schedulers.Schedulers;

public class CheckPhoneVerificationRestGateway implements CheckPhoneVerificationGateway {
    private final CheckPhoneVerificationRetrofitGateway checkPhoneVerificationRetrofitGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public CheckPhoneVerificationRestGateway(BaseActivity baseActivity) {
        checkPhoneVerificationRetrofitGateway = QorumHttpClient.get().create(CheckPhoneVerificationRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(baseActivity);
    }

    @Override
    public Observable<CheckPhoneResponseVerification> check(CheckPhoneRequestVerification checkPhoneRequestVerification) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> addCheck(token, checkPhoneRequestVerification))
                .compose(TwilioRx401Policy.apply());
    }

    private Observable<CheckPhoneResponseVerification> addCheck(Token token, CheckPhoneRequestVerification checkPhoneRequestVerification) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> checkPhoneVerificationRetrofitGateway.check(tokenWithPrefix, userId, checkPhoneRequestVerification));
    }

}
