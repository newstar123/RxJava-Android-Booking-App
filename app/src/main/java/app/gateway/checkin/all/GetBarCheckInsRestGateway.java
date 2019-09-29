package app.gateway.checkin.all;

import java.util.List;

import app.core.checkin.friends.entity.CheckinsFriendModel;
import app.core.checkin.friends.gateway.GetBarCheckInsListGateway;
import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetBarCheckInsRestGateway implements GetBarCheckInsListGateway {
    private final GetBarCheckInsGateway gateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public GetBarCheckInsRestGateway(BaseActivity activity) {
        gateway = QorumHttpClient.get().create(GetBarCheckInsGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    private Observable<List<CheckinsFriendModel>> getCheckins(Token token, long barId) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return gateway.get(tokenWithPrefix, barId, "Y");
    }

    @Override public Observable<List<CheckinsFriendModel>> get(long barId) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> getCheckins(token, barId));
    }
}
