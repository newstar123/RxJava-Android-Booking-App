package app.gateway.payment.get;


import app.core.init.token.entity.Token;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.gateway.GetPaymentsGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetPaymentsRestGateway implements GetPaymentsGateway{
    private final GetPaymentsRetrofitGateway getPaymentsRetrofitGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public GetPaymentsRestGateway(BaseActivity activity) {
        getPaymentsRetrofitGateway = QorumHttpClient.get().create(GetPaymentsRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<GetPaymentCardsModel> get() {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(this::getPayments)
                .compose(Rx401Policy.apply());
    }

    private Observable<GetPaymentCardsModel> getPayments(Token token) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> getPaymentsRetrofitGateway.get(tokenWithPrefix, userId));
    }
}
