package app.core.bars.locations.get;

import android.content.Context;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.bars.locations.entity.LocationsModel;
import app.gateway.bars.locations.get.GetCommonLocationListGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetBarLocationListInteractor implements BaseOutputInteractor<Observable<List<LocationsModel>>> {
    private final GetCommonLocationListGateway locationListGateway;
    private final CheckNetworkPermissionGateway networkPermissionGateway;

    public GetBarLocationListInteractor(Context context) {
        locationListGateway = new  GetCommonLocationListGateway();
        networkPermissionGateway = new CheckNetworkPermissionGateway(context);
    }

    @Override public Observable<List<LocationsModel>> process() {
        return networkPermissionGateway.check().concatMap(is -> locationListGateway.get());
    }
}
