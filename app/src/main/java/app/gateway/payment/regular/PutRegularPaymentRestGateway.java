package app.gateway.payment.regular;


import app.core.init.token.entity.Token;
import app.core.payment.regular.gateway.PutRegularPaymentGateway;
import app.core.payment.regular.model.RegularPaymentRequest;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutRegularPaymentRestGateway implements PutRegularPaymentGateway {
    private final PutRegularPaymentRetrofitGateway putRegularPaymentRetrofitGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public PutRegularPaymentRestGateway(BaseActivity activity) {
        putRegularPaymentRetrofitGateway = QorumHttpClient.get().create(PutRegularPaymentRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<String> put(String cardToken) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(authToken -> putRegular(authToken, cardToken))
                .compose(Rx401Policy.apply());
    }

    private Observable<String> putRegular(Token authToken, String cardToken) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(authToken);
        RegularPaymentRequest regularPaymentRequest = new RegularPaymentRequest(cardToken);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> putRegularPaymentRetrofitGateway.put(tokenWithPrefix, userId, regularPaymentRequest))
                .map(r -> "");
    }


}
