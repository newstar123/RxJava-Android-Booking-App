package app.core.gcm.put.interactor;

import android.content.Context;

import app.core.BaseInteractor;
import app.core.login.check.CheckAccountInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.gcm.PutGcmTokenRestGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutGcmTokenInteractor implements BaseInteractor<String, Observable<EmptyResponse>> {
    private final AuthTokenWithContextGateway getAuthTokenGateway;
    private final PutGcmTokenRestGateway putGcmTokenGateway;

    public PutGcmTokenInteractor(Context context) {
        getAuthTokenGateway = new AuthTokenWithContextGateway(context);
        putGcmTokenGateway = new PutGcmTokenRestGateway(context);
    }

    @Override public Observable<EmptyResponse> process(String newToken) {
        return getAuthTokenGateway.get()
                .doOnNext(CheckAccountInteractor::checkLoggedIn)
                .observeOn(Schedulers.io())
                .concatMap(token -> putGcmToken(newToken));
    }

    private Observable<EmptyResponse> putGcmToken(String newToken) {
        return putGcmTokenGateway.put(newToken);
    }
}
