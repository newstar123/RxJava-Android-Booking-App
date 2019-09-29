package app.delivering.component.service.beacon.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import app.CustomApplication;
import app.core.beacon.start.interactor.context.PrepareContextStartBeaconsInteractor;
import app.delivering.component.service.beacon.broadcast.model.StopForegroundServiceEvent;
import app.delivering.mvp.network.events.InternetConnectedEvent;
import app.delivering.mvp.network.events.InternetErrorConnectionEvent;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BackgroundBroadcastReceiver extends BroadcastReceiver {
    public static final String STOP_SERVICE_INTENT = "app.delivering.component.service.beacon.broadcast.STOP_SERVICE_INTENT";
    public static final String CLEAR_NOTIFICATION_FILTER_ACTION = "service.beacon.broadcast.CLEAR_NOTIFICATION_FILTER_ACTION";
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    private boolean isConnected = true;

    private static final String GOOGLE_DNS_ADDRESS = "8.8.8.8";
    private static final int PORT_NUMBER = 53;
    private static final int TIMEOUT = 2000;


    @Override public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        switch (intent.getAction()){
            case STOP_SERVICE_INTENT:
                EventBus.getDefault().post(new StopForegroundServiceEvent(getId(intent)));
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                checkNetworkState(context);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                startService(context);
                break;
            case CLEAR_NOTIFICATION_FILTER_ACTION:
                QorumNotifier.clearNotification(context, NotificationType.toType(intent.getStringExtra(QorumNotifier.NOTIFICATION_KEY)));
                break;
        }
    }

    private int getId(Intent intent) {
        return intent.getIntExtra(NOTIFICATION_ID, 0);
    }

    private void checkNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean connectionChange = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (connectionChange != isConnected)
            if (connectionChange) {
                Observable.just(true)
                        .observeOn(Schedulers.io())
                        .map(isOk -> isActiveNetworkWorking())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setUpConnectionEvent, err -> setUpConnectionEvent(false), () -> {});
            } else {
                setUpConnectionEvent(false);
            }
        isConnected = connectionChange;
    }

    private void setUpConnectionEvent(boolean isConnectedToInternet) {
        if (isConnectedToInternet) {
            EventBus.getDefault().removeStickyEvent(InternetErrorConnectionEvent.class);
            EventBus.getDefault().postSticky(new InternetConnectedEvent());
        } else {
            EventBus.getDefault().removeStickyEvent(InternetConnectedEvent.class);
            EventBus.getDefault().postSticky(new InternetErrorConnectionEvent());
        }
    }

    private boolean isActiveNetworkWorking() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(GOOGLE_DNS_ADDRESS, PORT_NUMBER), TIMEOUT);
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startService(Context context) {
        new PrepareContextStartBeaconsInteractor(context).process()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(isBluetoothStateOk -> CustomApplication.get().getCheckInController().activateCheckInScanner(),
                        e -> {}, () -> {});
    }
}
