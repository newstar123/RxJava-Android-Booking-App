package app.delivering.mvp.bars.detail.init.action.presenter;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class BarDetailCheckinInitButtonPresenter extends BaseOutputPresenter<Observable<Long>> {

    public BarDetailCheckinInitButtonPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override public Observable<Long> process() {
        return Observable.just(QorumSharedCache.checkBarCacheId().get(BaseCacheType.LONG));
    }
}
