package app.core.bars.list.get.interactor;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.core.location.get.GetRxLocationInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.bars.list.get.GetBarListGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetBarListInteractor implements BaseOutputInteractor<Observable<List<BarModel>>> {
    public static final int RANGE_MILES = 10000;
    private final GetBarListGateway barListGateway;
    private CheckNetworkPermissionGateway networkPermission;
    private GetRxLocationInteractor rxLocationInteractor;

    public GetBarListInteractor(BaseActivity activity) {
        barListGateway = new GetBarListGateway();
        networkPermission = new CheckNetworkPermissionGateway(activity);
        rxLocationInteractor = new GetRxLocationInteractor(activity);
    }

    @Override public Observable<List<BarModel>> process() {
        return networkPermission.check()
        .concatMap(isGranted -> rxLocationInteractor.process())
        .concatMap(location -> barListGateway.get(location.getLatitude(), location.getLongitude(), RANGE_MILES));
    }
}
