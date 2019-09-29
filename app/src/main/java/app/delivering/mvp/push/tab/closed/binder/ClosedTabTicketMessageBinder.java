package app.delivering.mvp.push.tab.closed.binder;

import android.app.Service;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import app.BuildConfig;
import app.CustomApplication;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.bar.detail.receiver.ActivitiesBackStackReceiver;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.main.service.feedback.events.CheckOutEvent;
import app.delivering.mvp.push.tab.closed.events.ClosedTabMessageEvent;
import app.delivering.mvp.push.tab.closed.presenter.CloseTabMessagePresenter;
import app.delivering.mvp.tab.button.close.events.OnCloseTabEvent;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.enums.CloseTabMethods;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.qamode.log.LogToFileHandler;

public class ClosedTabTicketMessageBinder extends BaseServiceBinder {
    private final CloseTabMessagePresenter presenter;
    private boolean closedManually;
    private boolean isClosedByUberCall;

    public ClosedTabTicketMessageBinder(Service service) {
        super(service);
        presenter = new CloseTabMessagePresenter(service);
    }

    @Subscribe
    public void onReceive(ClosedTabMessageEvent event) {
        LogToFileHandler.addLog("Tab closed push was received");
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - TAB_TICKET_CLOSED push was received");
        presenter.process(event.getMessage())
                .subscribe(this::closeTabBy, this::onError, ()->{});
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onClose(OnCloseTabEvent event){
        isClosedByUberCall = event.isClosedByUberCall();
        EventBus.getDefault().removeStickyEvent(OnCloseTabEvent.class);
        this.closedManually = true;
    }

    private void closeTabBy(CheckInResponse response) {
        CustomApplication.get().getCheckInController().onCheckOut(response.getCheckin());
        if (response.getCheckin() != null) {
            if (!isClosedByUberCall)
                setUpCheckoutOptions(response);
            else
                sendInfoViaMixpanel(response);
        }
        sendBackStackReceiver();
        resetValues();
    }

    private void resetValues() {
        closedManually = false;
        isClosedByUberCall = false;
    }

    private void setUpCheckoutOptions(CheckInResponse response) {
        CheckOutEvent checkOutEvent = new CheckOutEvent();
        checkOutEvent.setCheckInResponse(response);
        EventBus.getDefault().postSticky(checkOutEvent);
    }

    private void sendInfoViaMixpanel(CheckInResponse response) {
        if (!closedManually) {
            MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getCloseTabEvent(CloseTabMethods.CLOSE_TAB_BY_BARTENDER, response));
        }
    }

    private void sendBackStackReceiver() {
        Intent intent = new Intent(ActivitiesBackStackReceiver.REMOVE_BAR_DETAIL_ACTIVITY_INTENT);
        LocalBroadcastManager.getInstance(getService()).sendBroadcast(intent);
    }

    private void onError(Throwable e) {
        if (BuildConfig.DEBUG) {
            if (e instanceof JSONException)
                Toast.makeText(getService(), "ClosedTab ID JSONException", Toast.LENGTH_LONG).show();
            if (e instanceof NullPointerException)
                Toast.makeText(getService(), "ClosedTab ID Empty Exception", Toast.LENGTH_LONG).show();
        }
    }

}
