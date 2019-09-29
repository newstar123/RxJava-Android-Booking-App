package app.core.bars.list.tracking.interactor;

import android.content.Context;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.core.location.get.context.GetRxLocationWithContextInteractor;
import app.gateway.bars.list.rest.GetBarListRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetGpsBarTrackingInteractor implements BaseOutputInteractor<Observable<List<BarModel>>> {
    public static final int TRACKING_RANGE_MILES = 1;
    private final GetBarListRestGateway barListGateway;
    private CheckNetworkPermissionGateway networkPermission;
    private GetRxLocationWithContextInteractor rxLocationInteractor;

    public GetGpsBarTrackingInteractor(Context context) {
        barListGateway = new GetBarListRestGateway();
        networkPermission = new CheckNetworkPermissionGateway(context);
        rxLocationInteractor = new GetRxLocationWithContextInteractor(context);
    }

    @Override public Observable<List<BarModel>> process() {
        return networkPermission.check()
        .concatMap(isGranted -> rxLocationInteractor.process())
        .concatMap(location -> barListGateway.get(location.getLatitude(), location.getLongitude(), TRACKING_RANGE_MILES));
    }
}
