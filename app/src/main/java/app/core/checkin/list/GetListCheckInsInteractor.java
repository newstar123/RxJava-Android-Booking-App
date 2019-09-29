package app.core.checkin.list;

import java.util.ArrayList;
import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.core.checkin.user.get.gateway.GetCheckInsGateway;
import app.core.init.token.entity.Token;
import app.core.login.facebook.entity.LoginResponse;
import app.delivering.component.BaseActivity;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.checkin.recent.GetCheckInsRestGateway;
import rx.Observable;

public class GetListCheckInsInteractor implements BaseOutputInteractor<Observable<List<GetCheckInsResponse>>> {
    private final GetCheckInsGateway getCheckInsRestGateway;
    private final AuthTokenWithContextGateway authTokenWithContextGateway;

    public GetListCheckInsInteractor(BaseActivity activity) {
        getCheckInsRestGateway = new GetCheckInsRestGateway(activity);
        authTokenWithContextGateway = new AuthTokenWithContextGateway(activity);
    }

    @Override
    public Observable<List<GetCheckInsResponse>> process() {
       return authTokenWithContextGateway.get()
                .concatMap(this::tryToLoadLastCheckIns);
    }

    private Observable<List<GetCheckInsResponse>> tryToLoadLastCheckIns(Token token) {
            if (token.getAuthToken().equalsIgnoreCase(LoginResponse.GUEST_TOKEN))
                return Observable.just(new ArrayList<>());
            else
                return getCheckInsRestGateway.get(3);
    }
}
