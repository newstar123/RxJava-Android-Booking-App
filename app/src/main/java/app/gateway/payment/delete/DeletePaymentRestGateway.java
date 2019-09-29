package app.gateway.payment.delete;


import app.core.init.token.entity.Token;
import app.core.payment.delete.gateway.DeletePaymentGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DeletePaymentRestGateway implements DeletePaymentGateway {
    private final DeletePaymentRetrofitGateway deletePaymentRetrofitGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public DeletePaymentRestGateway(BaseActivity activity) {
        deletePaymentRetrofitGateway = QorumHttpClient.get().create(DeletePaymentRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<String> delete(String cardToken) {
        return androidAuthTokenGateway.get().concatMap(authToken -> deletePayment(cardToken, authToken));
    }

    private Observable<String> deletePayment(String cardToken, Token authToken) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(authToken);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .observeOn(Schedulers.io())
                .concatMap(userId -> deletePaymentRetrofitGateway.delete(tokenWithPrefix, userId, cardToken))
                .map(r -> "");
    }
}
