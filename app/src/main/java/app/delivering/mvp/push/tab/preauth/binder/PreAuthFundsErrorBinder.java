package app.delivering.mvp.push.tab.preauth.binder;

import android.app.Service;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import java.util.HashMap;

import app.BuildConfig;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.push.tab.id.presenter.CheckInIdByGCMPushPresenter;
import app.delivering.mvp.push.tab.preauth.events.PreAuthFundsErrorEvent;
import app.qamode.log.LogToFileHandler;

public class PreAuthFundsErrorBinder extends BaseServiceBinder {
    private final CheckInIdByGCMPushPresenter checkInIdByGCMPushPresenter;

    public PreAuthFundsErrorBinder(Service service) {
        super(service);
        checkInIdByGCMPushPresenter = new CheckInIdByGCMPushPresenter(service);
    }

    @Subscribe
    public void onReceive(PreAuthFundsErrorEvent event) {
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - PRE_AUTH_FUNDS_ERROR push was received");
        checkInIdByGCMPushPresenter.process(event.getJsonMessage())
                .subscribe(response -> {
                    HashMap map = new HashMap();
                    map.put(QorumNotifier.CHECK_IN_VENDOR_NAME, response.getCheckin().getVendor().getName());
                    map.put(QorumNotifier.VENDOR_ID, response.getCheckin().getVendor().getId());
                    map.put(QorumNotifier.CHECK_IN_ID, response.getCheckin().getId());
                    QorumNotifier.notify(getService(), NotificationType.DECLINED_PAYMENT, map);
                }, this::onError, ()->{});
    }

    private void onError(Throwable e) {
        if (BuildConfig.DEBUG) {
            if (e instanceof JSONException)
                Toast.makeText(getService(), "UpdateTab ID JSONException", Toast.LENGTH_LONG).show();
            if (e instanceof NullPointerException)
                Toast.makeText(getService(), "UpdateTab ID Empty Exception", Toast.LENGTH_LONG).show();
        }
    }
}
