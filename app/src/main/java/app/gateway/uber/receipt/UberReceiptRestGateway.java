package app.gateway.uber.receipt;

import com.uber.sdk.android.core.auth.AccessTokenManager;

import app.CustomApplication;
import app.core.uber.mock.ride.entity.UberReceiptResponse;
import app.core.uber.mock.ride.gateway.UberReceiptGateway;
import app.gateway.rest.client.UberHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;


public class UberReceiptRestGateway implements UberReceiptGateway {
    private final UberReceiptRetrofitGateway uberReceiptRetrofitGateway;
    private final AccessTokenManager accessTokenManager;

    public UberReceiptRestGateway() {
        uberReceiptRetrofitGateway = UberHttpClient.get().create(UberReceiptRetrofitGateway.class);
        accessTokenManager = new AccessTokenManager(CustomApplication.get());
    }

    @Override public Observable<UberReceiptResponse> get(String uberRideId) {
        String token = accessTokenManager.getAccessToken().getToken();
        String tokenWithPrefix = UberHttpClient.createTokenWithPrefix(token);
        return uberReceiptRetrofitGateway.get(tokenWithPrefix, uberRideId)
                .subscribeOn(Schedulers.io());
    }


}
