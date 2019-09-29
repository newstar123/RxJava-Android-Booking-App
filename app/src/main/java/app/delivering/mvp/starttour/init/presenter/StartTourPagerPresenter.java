package app.delivering.mvp.starttour.init.presenter;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class StartTourPagerPresenter extends BasePresenter<Integer, Observable<Integer>> {

    public StartTourPagerPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override public Observable<Integer> process(Integer integer) {
        return Observable.just((QorumSharedCache.checkSettingsStartTour().save(BaseCacheType.INT, integer)))
                .map(val -> integer);
    }
}
