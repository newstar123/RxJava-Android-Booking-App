package app.delivering.mvp.push.tab.update.binder;

import android.app.Service;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;

import app.BuildConfig;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.push.tab.id.presenter.CheckInIdByGCMPushPresenter;
import app.delivering.mvp.push.tab.update.events.UpdateTabMessageEvent;
import app.delivering.mvp.tab.init.events.UpdateTabEvent;
import app.qamode.log.LogToFileHandler;

public class UpdateTabTicketMessageBinder extends BaseServiceBinder {
    private final CheckInIdByGCMPushPresenter checkInIdByGCMPushPresenter;

    public UpdateTabTicketMessageBinder(Service service) {
        super(service);
        checkInIdByGCMPushPresenter = new CheckInIdByGCMPushPresenter(service);
    }

    @Subscribe
    public void onReceive(UpdateTabMessageEvent event) {
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - TAB_TICKET_UPDATED push was received");
        checkInIdByGCMPushPresenter.process(event.getJsonMessage())
                .subscribe(this::updateTabBy, this::onError, ()->{});
    }

    private void updateTabBy(CheckInResponse response) {
        UpdateTabEvent event = new UpdateTabEvent();
        event.setCheckIn(response);
        EventBus.getDefault().post(event);
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
