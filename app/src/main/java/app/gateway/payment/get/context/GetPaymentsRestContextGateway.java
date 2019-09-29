package app.gateway.payment.get.context;


import android.content.Context;

import app.core.init.token.entity.Token;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.gateway.GetPaymentsGateway;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.payment.get.GetPaymentsRetrofitGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetPaymentsRestContextGateway implements GetPaymentsGateway{
    private final GetPaymentsRetrofitGateway getPaymentsRetrofitGateway;
    private final AuthTokenWithContextGateway androidAuthTokenGateway;

    public GetPaymentsRestContextGateway(Context context) {
        getPaymentsRetrofitGateway = QorumHttpClient.get().create(GetPaymentsRetrofitGateway.class);
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
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
