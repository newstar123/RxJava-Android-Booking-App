package app.delivering.mvp.ride.mock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.widget.Toast;

import app.R;
import app.core.uber.mock.ride.entity.UberReceiptResponse;
import app.core.uber.mock.ride.interactor.UberMockRideInteractor;
import app.delivering.mvp.BaseServiceBinder;
import rx.Subscription;


public class MockRideBinder extends BaseServiceBinder {
    private final UberMockRideInteractor uberMockRideInteractor;
    private Subscription subscription;

    public MockRideBinder(Service service) {
        super(service);
        uberMockRideInteractor = new UberMockRideInteractor(service);
    }

    public void process(String uberRideId) {
        if (subscription == null || subscription.isUnsubscribed())
            subscription =  uberMockRideInteractor.process(uberRideId)
                .subscribe(this::onResult, this::onError);
    }

    private void onResult(UberReceiptResponse uberReceiptResponse) {
        showNotification(uberReceiptResponse);
        getService().stopSelf();
    }

    private void showNotification(UberReceiptResponse uberReceiptResponse) {
        Notification n = new Notification.Builder(getService())
                .setContentTitle("The test Uber ride from Qorum app has finished")
                .setSmallIcon(R.drawable.icon_launcher)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getService().getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(getService(), "Mock ride proccess was failed", Toast.LENGTH_LONG).show();
        getService().stopSelf();
    }

}
