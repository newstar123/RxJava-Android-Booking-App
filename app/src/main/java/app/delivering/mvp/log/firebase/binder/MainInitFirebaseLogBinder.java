package app.delivering.mvp.log.firebase.binder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.core.analytics.LogAnalyticsInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.service.init.events.ActivateRootServicesEvent;

public class MainInitFirebaseLogBinder extends BaseBinder {
    private final LogAnalyticsInteractor logAnalyticsInteractor;

    public MainInitFirebaseLogBinder(BaseActivity activity) {
        super(activity);
        logAnalyticsInteractor = new LogAnalyticsInteractor(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(ActivateRootServicesEvent event) {
        logAnalyticsInteractor.process().subscribe(aBoolean -> {}, e->{}, ()->{});
    }
}
