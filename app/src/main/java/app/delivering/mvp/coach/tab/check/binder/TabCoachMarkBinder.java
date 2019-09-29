package app.delivering.mvp.coach.tab.check.binder;

import java.util.concurrent.TimeUnit;

import app.core.coachmark.tab.get.GetTabCoachMarkInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.coach.tab.TabCoachMarkFragment;
import app.delivering.mvp.BaseBinder;
import rx.android.schedulers.AndroidSchedulers;

public class TabCoachMarkBinder extends BaseBinder {
    private final GetTabCoachMarkInteractor getTabCoachMarkInteractor;

    public TabCoachMarkBinder(BaseActivity activity) {
        super(activity);
        getTabCoachMarkInteractor = new GetTabCoachMarkInteractor();
    }

    @Override public void afterViewsBounded() {
        getTabCoachMarkInteractor.process()
                .filter(aBoolean -> !aBoolean)
                .delay(4, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> showCoachMark(), e -> {}, () -> {});
    }

    private void showCoachMark() {
        getActivity().start(new TabCoachMarkFragment());
    }
}
