package app.core.location.get.context;

import android.content.Context;
import android.location.Location;

import app.core.location.get.GetRxLocationGateway;
import app.delivering.mvp.BaseContextOutputPresenter;
import app.gateway.location.current.GetCurrentRxLocationGateway;
import app.gateway.permissions.location.context.CheckContextGPSPermissionGateway;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class GetRxLocationWithContextInteractor extends BaseContextOutputPresenter<Observable<Location>> {
    private final CheckContextGPSPermissionGateway gpsPermissionGateway;
    private final GetRxLocationGateway rxLocationGateway;

    public GetRxLocationWithContextInteractor(Context context) {
        super(context);
        rxLocationGateway = new GetCurrentRxLocationGateway(context);
        gpsPermissionGateway = new CheckContextGPSPermissionGateway(context);
    }

    @Override public Observable<Location> process() {
        return Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(isGranted -> gpsPermissionGateway.check())
                .concatMap(isGranted -> rxLocationGateway.get());
    }
}
