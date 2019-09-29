package app.delivering.mvp.main.coach.binder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import app.core.coachmark.ProfileCoachMarkInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.coach.profile.ProfileCoachMarkFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.coach.profile.check.events.CheckSecondLaunchEvent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class CheckCoachMarkBinder extends BaseBinder {
    private ProfileCoachMarkInteractor profileCoachMarkInteractor;
    private final static Integer SECOND_LAUNCH = 2;

    public CheckCoachMarkBinder(BaseActivity activity) {
        super(activity);
        profileCoachMarkInteractor = new ProfileCoachMarkInteractor(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkSecondLaunch(CheckSecondLaunchEvent event) {
        setUpInteractors();
    }


    private void setUpInteractors() {
        profileCoachMarkInteractor.process()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> { if (value.equals(SECOND_LAUNCH)) showCoachMark(); }, err -> {}, () -> {});
    }

    private void showCoachMark() {
        Observable.timer(4L, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(ok -> getActivity().start(new ProfileCoachMarkFragment()), err -> {}, () -> {});
    }

}
