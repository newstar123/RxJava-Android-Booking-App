package app.delivering.mvp.main.service.beacon.binder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.CustomApplication;
import app.core.beacon.start.interactor.PrepareStartBeaconsInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.service.beacon.events.ActivateBeaconServiceEvent;
import rx.android.schedulers.AndroidSchedulers;

public class StartBarNearbyServiceBinder extends BaseBinder {
    private final PrepareStartBeaconsInteractor startBeaconsInteractor;

    public StartBarNearbyServiceBinder(BaseActivity activity) {
        super(activity);
        startBeaconsInteractor = new PrepareStartBeaconsInteractor(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActivate(ActivateBeaconServiceEvent event) {
        startBeaconsInteractor.process()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> CustomApplication.get().getCheckInController().activateCheckInScanner(), e->{}, ()->{});
    }
}
