package app.gateway.permissions.storage;

import android.Manifest;

import com.tbruyelle.rxpermissions.RxPermissions;

import app.core.permission.storage.entitty.ExternalStoragePermissionException;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.CheckPermissionGateway;
import rx.Observable;
import rx.functions.Action1;

public class CheckExternalStoragePermissionGateway implements CheckPermissionGateway {
    private BaseActivity activity;

    public CheckExternalStoragePermissionGateway(BaseActivity activity) {
        this.activity = activity;
    }

    @Override public Observable<Boolean> check() {
        return new RxPermissions(activity).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .doOnNext(new Action1<Boolean>() {
                    @Override public void call(Boolean isGranted) {
                        if (!isGranted)
                            throw new ExternalStoragePermissionException();
                    }
                });
    }
}
