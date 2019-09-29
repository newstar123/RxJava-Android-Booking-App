package app.delivering.mvp.bars.detail.checkin.click.presenter;

import app.core.checkin.cacheid.GetSharedCheckInIdInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.bars.detail.checkin.click.model.BarCheckinIdsModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class BarDetailCheckinClickButtonPresenter extends BaseOutputPresenter<Observable<BarCheckinIdsModel>> {
    private GetSharedCheckInIdInteractor checkInIdInteractor;

    public BarDetailCheckinClickButtonPresenter(BaseActivity activity) {
        super(activity);
        checkInIdInteractor = new GetSharedCheckInIdInteractor();
    }

    @Override public Observable<BarCheckinIdsModel> process() {
        return Observable.zip(Observable.just(QorumSharedCache.checkBarCacheId().get(BaseCacheType.LONG)), checkInIdInteractor.process(), this::idsModel);
    }

    private BarCheckinIdsModel idsModel(Long barId, Long checkInId) {
        BarCheckinIdsModel idsModel = new BarCheckinIdsModel();
        idsModel.setBarId(barId);
        idsModel.setCheckinId(checkInId);
        return idsModel;
    }
}
