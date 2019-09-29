package app.delivering.mvp.beacons.invitation.binder;

import android.app.Service;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import app.CustomApplication;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.beacons.invitation.events.CheckInInvitationEvent;
import app.delivering.mvp.beacons.invitation.presenter.CheckInInvitationPresenter;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.qamode.log.LogToFileHandler;
import rx.android.schedulers.AndroidSchedulers;

public class CheckInInvitationBinder extends BaseServiceBinder {
    private final CheckInInvitationPresenter presenter;

    public CheckInInvitationBinder(Service service) {
        super(service);
        presenter = new CheckInInvitationPresenter(service);
    }

    @Subscribe
    public void onInvitation(CheckInInvitationEvent event) {
        presenter.process(event)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(invitationEvent -> {
                    LogToFileHandler.addLog("Beacons Scanner sent an Invite message - " + invitationEvent.getName());
                    HashMap map = new HashMap();
                    map.put(QorumNotifier.CHECK_IN_VENDOR_NAME, invitationEvent.getName());
                    map.put(QorumNotifier.VENDOR_ID, invitationEvent.getId());
                    QorumNotifier.notify(CustomApplication.get(), NotificationType.BEACON_TAB_OPENING, map);
                }, e->{}, ()->{});
    }
}
