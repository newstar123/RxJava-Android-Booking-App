package app.gateway.permissions.phone;

import android.Manifest;

import com.tbruyelle.rxpermissions.RxPermissions;

import app.core.permission.entity.CallPhonePermissionException;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.CheckPermissionGateway;
import rx.Observable;
import rx.functions.Action1;

public class CheckCallPhonePermissionGateway implements CheckPermissionGateway {
    private RxPermissions rxPermissions;
    private BaseActivity activity;

    public CheckCallPhonePermissionGateway(BaseActivity activity) {
        this.activity = activity;
    }

    @Override public Observable<Boolean> check() {
        rxPermissions = new RxPermissions(activity);
        return rxPermissions.request(Manifest.permission.CALL_PHONE)
                .doOnNext(new Action1<Boolean>() {
                    @Override public void call(Boolean isGranted) {
                        if (!isGranted)
                            throw new CallPhonePermissionException();
                    }
                });
    }
}
