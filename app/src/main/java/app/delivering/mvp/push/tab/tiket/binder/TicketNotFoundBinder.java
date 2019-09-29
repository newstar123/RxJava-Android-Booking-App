package app.delivering.mvp.push.tab.tiket.binder;

import android.app.Service;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import app.CustomApplication;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.service.beacon.broadcast.RangeCheckInReceiver;
import app.delivering.mvp.BaseServiceBinder;
import app.delivering.mvp.main.service.feedback.events.CheckOutEvent;
import app.delivering.mvp.notification.notifier.NotificationType;
import app.delivering.mvp.notification.notifier.QorumNotifier;
import app.delivering.mvp.push.tab.tiket.events.TicketNotFoundEvent;
import app.delivering.mvp.push.tab.tiket.presenter.CloseEmptyTicketPresenter;
import app.qamode.log.LogToFileHandler;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class TicketNotFoundBinder extends BaseServiceBinder {
    private final CloseEmptyTicketPresenter emptyTicketPresenter;

    public TicketNotFoundBinder(Service service) {
        super(service);
        emptyTicketPresenter = new CloseEmptyTicketPresenter(service);
    }

    @Subscribe
    public void onReceive(TicketNotFoundEvent event) {
        LogToFileHandler.addLog("TicketNotFound push was received");
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - TAB_TICKET_CLOSED_EMPTY push was received");
        getService().sendBroadcast(new Intent(RangeCheckInReceiver.ON_CHECK_OUT_ACTION));
        emptyTicketPresenter.process(event.getMessage())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateTabBy, this::onError, ()->{});
    }

    private void updateTabBy(CheckInResponse response) {
        CustomApplication.get().getCheckInController().onCheckOut(response.getCheckin());
        CheckOutEvent checkOutEvent = new CheckOutEvent();
        checkOutEvent.setCheckInResponse(response);
        EventBus.getDefault().postSticky(checkOutEvent);
    }

    private void onError(Throwable e) {
        if (e instanceof HttpException && ((HttpException)e).code() == 409) {
            LogToFileHandler.addLog("TicketNotFound, error - 409");
            QorumNotifier.notify(CustomApplication.get(), NotificationType.TICKET_NOT_FOUND_ERROR, new HashMap());
        }
        LogToFileHandler.addLog("TicketNotFound, error - " + e.getMessage());
    }
}
