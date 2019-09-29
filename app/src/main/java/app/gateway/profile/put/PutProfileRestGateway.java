package app.gateway.profile.put;

import app.core.init.token.entity.Token;
import app.core.login.facebook.entity.LoginResponse;
import app.core.profile.put.entity.PutProfileModel;
import app.core.profile.put.gateway.PutProfileGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutProfileRestGateway implements PutProfileGateway {
    private final PutProfileRetrofitGateway putProfileGateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public PutProfileRestGateway(BaseActivity activity) {
        putProfileGateway = QorumHttpClient.get().create(PutProfileRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<LoginResponse> put(long userId, PutProfileModel model) {
        return androidAuthTokenGateway.get().observeOn(Schedulers.io()).concatMap(token -> putProfile(token, userId, model));
    }

    private Observable<LoginResponse> putProfile(Token token, long userId, PutProfileModel model) {
        String tokenWithPrefix = QorumHttpClient.HEADER_AUTHORIZATION_PREFIX + token.getAuthToken();
        return putProfileGateway.put(tokenWithPrefix, userId, model)
                .compose(Rx401Policy.apply());
    }
}
