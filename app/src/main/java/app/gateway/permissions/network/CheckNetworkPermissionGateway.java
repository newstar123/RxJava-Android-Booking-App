package app.gateway.permissions.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import app.core.permission.entity.NetworkSettingsException;
import app.gateway.permissions.CheckPermissionGateway;
import rx.Observable;

public class CheckNetworkPermissionGateway implements CheckPermissionGateway {

    private Context context;

    public CheckNetworkPermissionGateway(Context context) {
        this.context = context;
    }

    @Override public Observable<Boolean> check() {
        return Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            subscriber.onNext(isConnected);
            subscriber.onCompleted();
        }).doOnNext(isGranted -> {
            if (!isGranted) throw new NetworkSettingsException();
        });
    }
}
