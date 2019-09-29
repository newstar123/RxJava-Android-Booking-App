package app.delivering.mvp.push.root.binder;

import android.app.Service;

import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import app.R;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.push.ride.complete.to.events.PushRideToBarCompleteEvent;
import app.delivering.mvp.push.root.model.RootPushNotificationResponse;
import app.delivering.mvp.push.root.presenter.RootNotificationPresenter;
import app.delivering.mvp.push.tab.closed.events.ClosedTabMessageEvent;
import app.delivering.mvp.push.tab.preauth.events.PreAuthFundsErrorEvent;
import app.delivering.mvp.push.tab.tiket.events.TicketNotFoundEvent;
import app.delivering.mvp.push.tab.update.events.UpdateTabMessageEvent;
import app.qamode.log.LogToFileHandler;

public class RootNotificationBinder extends BaseServiceBinder {
    private RootNotificationPresenter presenter;

    public RootNotificationBinder(Service service) {
        super(service);
        presenter = new RootNotificationPresenter(service);
    }

    @Subscribe
    public void onReceiveMessage(RemoteMessage message) {
        presenter.process(message)
                .subscribe(this::parseByType, e -> { }, () -> { });
    }

    private void parseByType(RootPushNotificationResponse response) {
        switch (response.getStatus()) {
            case RIDE_TO_VENUE_COMPLETED:
                EventBus.getDefault().post(new PushRideToBarCompleteEvent(response.getJsonMessage()));
                break;
            case RIDE_FROM_VENUE_COMPLETED:
                //TODO: check iOS example for end of the ride from the venue
               // EventBus.getDefault().post(new PushRideFromBarCompleteEvent(response.getJsonMessage()));
                break;
            case TAB_TICKET_UPDATED:
                EventBus.getDefault().post(new UpdateTabMessageEvent(response.getJsonMessage()));
                break;
            case TAB_TICKET_CLOSED:
                EventBus.getDefault().post(new ClosedTabMessageEvent(response.getJsonMessage()));
                break;
            case PRE_AUTH_FUNDS_ERROR:
                EventBus.getDefault().post(new PreAuthFundsErrorEvent(response.getJsonMessage()));
                break;
            case TAB_TICKET_CLOSED_EMPTY:
                EventBus.getDefault().post(new TicketNotFoundEvent(response.getJsonMessage()));
                break;
            case CURRENT_POS_ERROR:
                sendMessage(NotificationType.POS_ERROR_MESSAGE, getService().getString(R.string.current_pos_error_500_1_6));
                break;
            case POS_ERROR_CHECKIN:
                sendMessage(NotificationType.POS_ERROR_MESSAGE, response.getServerMessage());
                break;
            case POS_ERROR_CLOSE_CHECKIN:
                sendMessage(NotificationType.POS_ERROR_MESSAGE, response.getServerMessage());
                break;
            case POS_ERROR_WITHOUT_CHECKIN:
                sendMessage(NotificationType.POS_ERROR_MESSAGE, response.getServerMessage());
                break;
            default:
                sendMessage(NotificationType.DEF, response.getServerMessage());
        }
    }

    private void sendMessage(NotificationType messageType, String message) {
        LogToFileHandler.addLog("QorumNotifier(RootNotificationBinder) - DEF server message: ".concat(message));
        HashMap content = new HashMap();
        content.put(QorumNotifier.MESSAGE, message);
        QorumNotifier.notify(getService(), messageType, content);
    }
}
