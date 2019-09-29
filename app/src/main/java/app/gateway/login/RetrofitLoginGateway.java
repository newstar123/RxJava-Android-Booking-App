package app.gateway.login;

import app.core.login.facebook.entity.LoginRequest;
import app.core.login.facebook.entity.LoginResponse;
import app.core.login.facebook.gateway.LoginGateway;
import app.gateway.rest.client.QorumHttpClient;
import retrofit2.http.Body;
import rx.Observable;
import rx.schedulers.Schedulers;


public class RetrofitLoginGateway implements LoginGateway {
    private final LoginGateway gateway;

    public RetrofitLoginGateway() {
        gateway = QorumHttpClient.get().create(LoginGateway.class);
    }

    @Override public Observable<LoginResponse> post(@Body LoginRequest loginRequest) {
        return gateway.post(loginRequest).subscribeOn(Schedulers.io());
    }
}
