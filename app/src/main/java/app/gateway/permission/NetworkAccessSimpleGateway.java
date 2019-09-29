package app.gateway.permission;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import app.core.permission.entity.NetworkSettingsException;
import app.core.permission.gateway.NetworkAccessGateway;
import app.delivering.component.BaseActivity;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class NetworkAccessSimpleGateway implements NetworkAccessGateway {
    private BaseActivity activity;

    public NetworkAccessSimpleGateway(BaseActivity activity) {
        this.activity = activity;
    }

    @Override public Observable<Boolean> check() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override public void call(Subscriber<? super Boolean> subscriber) {
                ConnectivityManager cm =
                        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                subscriber.onNext(isConnected);
                subscriber.onCompleted();
            }
        }).doOnNext(new Action1<Boolean>() {
            @Override public void call(Boolean isGranted) {
                if (!isGranted) throw new NetworkSettingsException();
            }
        });
    }
}
