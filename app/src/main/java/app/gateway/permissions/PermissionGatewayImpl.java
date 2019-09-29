package app.gateway.permissions;

import android.content.Context;

import com.tbruyelle.rxpermissions.RxPermissions;

import app.delivering.component.BaseActivity;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public abstract class PermissionGatewayImpl implements CheckPermissionGateway {
    private final BaseActivity activity;
    private final String[] permissionName;
    private final RxPermissions rxPermissions;

    public PermissionGatewayImpl(BaseActivity activity, String... permissionName) {
        this.permissionName = permissionName;
        this.activity = activity;
        rxPermissions = new RxPermissions(activity);
    }

    @Override
    public Observable<Boolean> check() {
        return Observable.just(rxPermissions.isGranted(permissionName[0]))
                .single()
                .concatMap(isGranted -> isGranted ?
                        Observable.just(true)
                        : askLocationPermission());
    }

    public Observable<Boolean> askLocationPermission() {
        return rxPermissions.request(permissionName)
                        .concatMap(isGranted ->
                                isGranted ?
                                        Observable.just(QorumSharedCache.checkPermissionsAccount().save(BaseCacheType.INT, PermissionState.ALLOWED.getIndex()))
                                                .doOnNext(index -> onAccessGranted(activity))
                                                .map(index -> true)
                                        : checkPermissionState());
    }

    private Observable<Boolean> checkPermissionState() {
        return Observable.zip(
                rxPermissions.shouldShowRequestPermissionRationale(activity, permissionName),
                Observable.just((int)QorumSharedCache.checkPermissionsAccount().get(BaseCacheType.INT)), (shouldShowRationale, integer) ->
                        isAskingFirstTime(PermissionState.convertToState(integer))
                                ? PermissionState.DENIED
                                : shouldShowRationale
                                ? PermissionState.DENIED
                                : PermissionState.DO_NOT_ASK_AGAIN)
                .concatMap(this::saveCurrentPermissionState)
                .doOnNext(state -> {
                    if (state == PermissionState.DENIED)
                        onAccessDenied(activity);
                    else
                        onDoNotAskAgainActivated(activity);
                })
                .filter(permissionState -> false)
                .map(permissionState -> false);
    }

    private Boolean isAskingFirstTime(PermissionState state) {
        return state == PermissionState.NO_SAVED_VALUE;
    }

    private Observable<PermissionState> saveCurrentPermissionState(PermissionState permissionState) {
        return Observable.just(QorumSharedCache.checkPermissionsAccount().save(BaseCacheType.INT, permissionState.getIndex()))
                .map(integer -> permissionState);
    }

    public abstract void onAccessGranted(Context context);

    public abstract void onAccessDenied(Context context);

    public abstract void onDoNotAskAgainActivated(Context context);
}
