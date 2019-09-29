package app.gateway.permissions.location.context;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.ActivityCompat;

import app.core.permission.entity.LocationPermissionException;
import app.gateway.permissions.CheckPermissionGateway;
import rx.Observable;

public class CheckContextGPSPermissionGateway implements CheckPermissionGateway {
    private Context context;

    public CheckContextGPSPermissionGateway(Context context) {
        this.context = context;
    }

    @Override public Observable<Boolean> check() {
        return Observable.create(subscriber -> {
                int coareLocationState = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                int fineLocationState = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if (coareLocationState == context.getPackageManager().PERMISSION_GRANTED && fineLocationState == context.getPackageManager().PERMISSION_GRANTED){
                subscriber.onNext(true);
                subscriber.onCompleted();
            } else
                subscriber.onError(new LocationPermissionException());
        });
    }
}
