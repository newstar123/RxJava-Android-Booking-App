package app.delivering.mvp.coach.checkin.binder;

import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.core.coach.checkin.check.interactor.CheckFirstCheckInCoachMarkInteractor;
import app.core.coach.entity.BooleanResponse;
import app.delivering.component.BaseActivity;
import app.delivering.component.coach.checkin.FirstCheckInCoachMarkFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabClickEvent;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabEvent;
import rx.android.schedulers.AndroidSchedulers;

public class FirstCheckInCoachMarkBinder extends BaseBinder {
    private final CheckFirstCheckInCoachMarkInteractor interactor;
    public final static String CURRENT_BAR_ID = "current_bar_id";

    public FirstCheckInCoachMarkBinder(BaseActivity activity) {
        super(activity);
        interactor = new CheckFirstCheckInCoachMarkInteractor(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckCoachMark(OpenTabClickEvent event) {
        interactor.process()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> checkToShowCoachMark(response, event), e -> openTab(event), () -> {});
    }

    private void checkToShowCoachMark(BooleanResponse response, OpenTabClickEvent event) {
        if (response.isOk()) {
            showCoachMark(event.getCurrentBarId());
        } else
            openTab(event);
    }

    private void showCoachMark(long currentBarId) {
        FirstCheckInCoachMarkFragment fragment = new FirstCheckInCoachMarkFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(CURRENT_BAR_ID, currentBarId);
        fragment.setArguments(arguments);
        getActivity().add(fragment);
    }

    private void openTab(OpenTabClickEvent event) {
        OpenTabEvent clickEvent = new OpenTabEvent();
        clickEvent.setCurrentBarId(event.getCurrentBarId());
        EventBus.getDefault().postSticky(clickEvent);
    }
}
