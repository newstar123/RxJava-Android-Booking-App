package app.core.permission.interactor;

import app.core.BaseOutputInteractor;
import app.core.permission.gateway.NetworkAccessGateway;
import app.delivering.component.BaseActivity;
import app.gateway.location.settings.GoogleLocationSettingsGateway;
import app.gateway.permission.NetworkAccessSimpleGateway;
import app.gateway.permissions.account.CheckAccountPermissionGateway;
import app.gateway.permissions.location.CheckGPSPermissionGateway;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class PermissionInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private final GoogleLocationSettingsGateway locationSettingsGateway;
    private final CheckGPSPermissionGateway gpsPermissionGateway;
    private final CheckAccountPermissionGateway accountPermissionGateway;
    private NetworkAccessGateway networkAccessGateway;

    public PermissionInteractor(BaseActivity activity) {
        locationSettingsGateway = new GoogleLocationSettingsGateway(activity);
        networkAccessGateway = new NetworkAccessSimpleGateway(activity);
        gpsPermissionGateway = new CheckGPSPermissionGateway(activity);
        accountPermissionGateway = new CheckAccountPermissionGateway(activity);
    }

    @Override public Observable<Boolean> process() {
        return  accountPermissionGateway.check()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(isGranted -> gpsPermissionGateway.check())
                .concatMap(isGranted -> locationSettingsGateway.get())
                .concatMap(isGranted -> networkAccessGateway.check());
    }
}
