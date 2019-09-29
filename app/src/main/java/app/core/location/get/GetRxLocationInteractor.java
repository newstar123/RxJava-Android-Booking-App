package app.core.location.get;

import android.location.Location;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.location.current.GetCurrentRxLocationGateway;
import app.gateway.location.save.PutSharedLastLocationGateway;
import app.gateway.location.settings.GoogleLocationSettingsGateway;
import app.gateway.permissions.location.CheckGPSPermissionGateway;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class GetRxLocationInteractor extends BaseOutputPresenter<Observable<Location>> {
    private CheckGPSPermissionGateway gpsPermissionGateway;
    private final GoogleLocationSettingsGateway locationSettingsGateway;
    private GetRxLocationGateway rxLocationGateway;
    private PutSharedLastLocationGateway putSharedLastLocationGateway;

    public GetRxLocationInteractor(BaseActivity activity) {
        super(activity);
        rxLocationGateway = new GetCurrentRxLocationGateway(activity);
        gpsPermissionGateway = new CheckGPSPermissionGateway(activity);
        locationSettingsGateway = new GoogleLocationSettingsGateway(activity);
        putSharedLastLocationGateway = new PutSharedLastLocationGateway(activity);
    }

    @Override public Observable<Location> process() {
        return Observable.just(true)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(isGranted -> gpsPermissionGateway.check())
                .concatMap(isGranted -> locationSettingsGateway.get())
                .concatMap(isGranted -> rxLocationGateway.get())
                .concatMap(location -> putSharedLastLocationGateway.put(location));
    }
}
