package app.delivering.mvp.main.service.checkin.binder;

import android.content.Intent;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.delivering.component.BaseActivity;
import app.delivering.component.service.checkin.TabStatusForegroundService;
import app.delivering.component.tab.TabActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.open.model.ActiveCheckInVendorId;
import app.delivering.mvp.main.service.checkin.events.CheckActiveCheckInEvent;
import app.delivering.mvp.main.service.checkin.model.CheckActiveCheckInResult;
import app.delivering.mvp.main.service.checkin.presenter.StartCheckActiveCheckInPresenter;
import rx.android.schedulers.AndroidSchedulers;

public class StartCheckActiveCheckInBinder extends BaseBinder {
    private final StartCheckActiveCheckInPresenter presenter;

    public StartCheckActiveCheckInBinder(BaseActivity activity) {
        super(activity);
        presenter = new StartCheckActiveCheckInPresenter(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(CheckActiveCheckInEvent event) {
        presenter.process(event.getActiveCheckIn())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, e -> {}, () -> {});
    }

    private void show(CheckActiveCheckInResult result) {
        if (result.getActiveCheckInId() != 0)
            startForegroundCheckInService(result);
    }

    private void startForegroundCheckInService(CheckActiveCheckInResult result) {
        EventBus.getDefault().post(new ActiveCheckInVendorId(result.getBarId()));
        Intent startIntent = new Intent(getActivity(), TabStatusForegroundService.class);
        startIntent.setAction(TabStatusForegroundService.START_FOREGROUND_ACTION);
        startIntent.putExtra(TabActivity.TAB_BAR_NAME, result.getBarName());
        startIntent.putExtra(TabActivity.TAB_CHECK_IN_ID, result.getActiveCheckInId());
        getActivity().startService(startIntent);
    }
}
