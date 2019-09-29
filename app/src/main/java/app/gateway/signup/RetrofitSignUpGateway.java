package app.gateway.signup;


import app.core.login.facebook.entity.LoginResponse;
import app.core.login.facebook.entity.SignUpRequest;
import app.core.login.facebook.gateway.SignUpGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RetrofitSignUpGateway implements SignUpGateway{
    private final SignUpGateway gateway;

    public RetrofitSignUpGateway() {
        gateway = QorumHttpClient.get().create(SignUpGateway.class);
    }

    @Override public Observable<LoginResponse> post(SignUpRequest request) {
        return gateway.post(request).subscribeOn(Schedulers.io());
    }
}
