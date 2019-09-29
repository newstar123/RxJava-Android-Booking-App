package app.gateway.payment.add;


import app.core.init.token.entity.Token;
import app.core.payment.add.entity.AddPaymentTokenModel;
import app.core.payment.add.gateway.AddPaymentGateway;
import app.core.payment.get.entity.GetPaymentCardModel;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AddPaymentRestGateway implements AddPaymentGateway {
    private final AddPaymentRetrofitGateway addPaymentGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public AddPaymentRestGateway(BaseActivity activity) {
        addPaymentGateway = QorumHttpClient.get().create(AddPaymentRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<GetPaymentCardModel> add(AddPaymentTokenModel model) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> addPayment(token, model))
                .compose(Rx401Policy.apply());
    }

    private Observable<GetPaymentCardModel> addPayment(Token token, AddPaymentTokenModel model) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> addPaymentGateway.post(tokenWithPrefix, userId, model));
    }
}
