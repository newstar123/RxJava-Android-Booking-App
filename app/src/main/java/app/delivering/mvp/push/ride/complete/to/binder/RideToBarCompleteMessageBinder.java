package app.delivering.mvp.push.ride.complete.to.binder;

import android.app.Service;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.CustomApplication;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.payment.get.entity.NoPaymentException;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.push.ride.complete.to.events.PushRideToBarCompleteEvent;
import app.delivering.mvp.push.ride.complete.to.presenter.AutoCheckInAfterRideToBarPresenter;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class RideToBarCompleteMessageBinder extends BaseServiceBinder {
    private static final int TICKET_NAME_ERROR = 409;
    private static final String PUSH_BAR = "venue";
    private static final String PUSH_BAR_ID = "id";
    private static final String PUSH_BAR_NAME = "name";
    private final AutoCheckInAfterRideToBarPresenter presenter;

    public RideToBarCompleteMessageBinder(Service service) {
        super(service);
        presenter = new AutoCheckInAfterRideToBarPresenter(service);
    }

    @Subscribe
    public void onReceiveMessage(PushRideToBarCompleteEvent event) {
        presenter.process(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::show, e->onError(e, event), ()->{});
    }

    private void show(CheckInResponse response) {
        HashMap map = new HashMap();
        map.put(QorumNotifier.CHECK_IN_VENDOR_NAME, response.getCheckin().getVendor().getName());
        map.put(QorumNotifier.VENDOR_ID, response.getCheckin().getVendor().getId());
        map.put(QorumNotifier.CHECK_IN_ID, response.getCheckin().getId());
        QorumNotifier.notify(getService(), NotificationType.BEACON_TAB_OPENED, new HashMap());
    }

    private void onError(Throwable error, PushRideToBarCompleteEvent event) {
        try {
            JSONObject barJson = event.getMessage().getJSONObject(PUSH_BAR);
            long id = barJson.getLong(PUSH_BAR_ID);
            String name = barJson.getString(PUSH_BAR_NAME);
            if (error instanceof HttpException && ((HttpException) error).code() == TICKET_NAME_ERROR) {
                HashMap map = new HashMap();
                map.put(QorumNotifier.CHECK_IN_VENDOR_NAME, name);
                map.put(QorumNotifier.VENDOR_ID, id);
                QorumNotifier.notify(CustomApplication.get(), NotificationType.BEACONS_TAB_OPENING_ERROR_409, map);
            } else if (error instanceof NoPaymentException) {
                HashMap map = new HashMap();
                map.put(QorumNotifier.VENDOR_ID, id);
                QorumNotifier.notify(CustomApplication.get(), NotificationType.PAYMENT_METHOD_ERROR, map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
