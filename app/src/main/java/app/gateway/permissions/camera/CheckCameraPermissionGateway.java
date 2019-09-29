package app.gateway.permissions.camera;

import android.Manifest;

import com.tbruyelle.rxpermissions.RxPermissions;

import app.core.permission.camera.entity.CameraPermissionException;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.CheckPermissionGateway;
import rx.Observable;

public class CheckCameraPermissionGateway implements CheckPermissionGateway {
    private BaseActivity activity;

    public CheckCameraPermissionGateway(BaseActivity activity) {
        this.activity = activity;
    }

    @Override public Observable<Boolean> check() {
        return new RxPermissions(activity).request(Manifest.permission.CAMERA)
                .doOnNext(isGranted -> {
                    if (!isGranted)
                        throw new CameraPermissionException();
                });
    }
}
