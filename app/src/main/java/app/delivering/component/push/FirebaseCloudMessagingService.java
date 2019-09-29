package app.delivering.component.push;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import app.core.gcm.put.interactor.PutGcmTokenInteractor;
import app.delivering.mvp.push.ride.complete.to.binder.RideToBarCompleteMessageBinder;
import app.delivering.mvp.push.root.binder.RootNotificationBinder;
import app.delivering.mvp.push.tab.closed.binder.ClosedTabTicketMessageBinder;
import app.delivering.mvp.push.tab.preauth.binder.PreAuthFundsErrorBinder;
import app.delivering.mvp.push.tab.tiket.binder.TicketNotFoundBinder;
import app.delivering.mvp.push.tab.update.binder.UpdateTabTicketMessageBinder;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {
    private RootNotificationBinder rootNotificationBinder;
    private RideToBarCompleteMessageBinder toBarCompleteMessageBinder;
    private UpdateTabTicketMessageBinder updateTabTicketMessageBinder;
    private ClosedTabTicketMessageBinder closedTabTicketMessageBinder;
    private PreAuthFundsErrorBinder preAuthFundsErrorBinder;
    private TicketNotFoundBinder ticketNotFoundBinder;

    @Override public void onCreate() {
        super.onCreate();
        rootNotificationBinder = new RootNotificationBinder(this);
        EventBus.getDefault().register(rootNotificationBinder);
        toBarCompleteMessageBinder = new RideToBarCompleteMessageBinder(this);
        EventBus.getDefault().register(toBarCompleteMessageBinder);
        updateTabTicketMessageBinder = new UpdateTabTicketMessageBinder(this);
        EventBus.getDefault().register(updateTabTicketMessageBinder);
        closedTabTicketMessageBinder = new ClosedTabTicketMessageBinder(this);
        EventBus.getDefault().register(closedTabTicketMessageBinder);
        preAuthFundsErrorBinder = new PreAuthFundsErrorBinder(this);
        EventBus.getDefault().register(preAuthFundsErrorBinder);
        ticketNotFoundBinder = new TicketNotFoundBinder(this);
        EventBus.getDefault().register(ticketNotFoundBinder);
    }

    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        new PutGcmTokenInteractor(getApplicationContext()).process(newToken)
                .subscribe(emptyResponse -> onTokenShouldBeReSent(false),
                        e -> onTokenShouldBeReSent(true), ()->{});
    }

    private void onTokenShouldBeReSent(Boolean shouldBeSaved) {
        QorumSharedCache.checkGCMTokenUpdates().save(BaseCacheType.BOOLEAN, shouldBeSaved);
    }

    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("NO_TICKET", "onMessageReceived");
        EventBus.getDefault().post(remoteMessage);
    }

    @Override public void onDestroy() {
        EventBus.getDefault().unregister(rootNotificationBinder);
        EventBus.getDefault().unregister(toBarCompleteMessageBinder);
        EventBus.getDefault().unregister(updateTabTicketMessageBinder);
        EventBus.getDefault().unregister(closedTabTicketMessageBinder);
        EventBus.getDefault().unregister(preAuthFundsErrorBinder);
        EventBus.getDefault().unregister(ticketNotFoundBinder);
        super.onDestroy();
    }

}
