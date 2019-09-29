package app.delivering.mvp.dialog.base.presenter;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.dialog.base.model.SharedCheckInIdsModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class BaseDialogIDsPresenter extends BaseOutputPresenter<Observable<SharedCheckInIdsModel>> {

    public BaseDialogIDsPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override
    public Observable<SharedCheckInIdsModel> process() {
        return Observable.zip(Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG)),
                Observable.just(QorumSharedCache.checkBarCacheId().get(BaseCacheType.LONG)), SharedCheckInIdsModel::new);
    }
}
