package app.core.checkin.friends.interactor;

import java.util.List;

import app.core.BaseInteractor;
import app.core.checkin.friends.entity.CheckinsFriendModel;
import app.delivering.component.BaseActivity;
import app.gateway.checkin.all.GetBarCheckInsRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetCheckInsInteractor implements BaseInteractor<Long, Observable<List<CheckinsFriendModel>>> {
    private GetBarCheckInsRestGateway gateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;

    public GetCheckInsInteractor(BaseActivity activity) {
        gateway = new GetBarCheckInsRestGateway(activity);
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
    }

    @Override public Observable<List<CheckinsFriendModel>> process(Long barId) {
        return networkPermissionGateway.check()
                .concatMap(isOk -> gateway.get(barId));
    }
}
