package app.delivering.mvp.bars.detail.checkin.open.presenter;

import app.core.checkin.user.post.entity.CheckInRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkin.user.post.interactor.CheckInInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabEvent;
import app.delivering.mvp.bars.detail.checkin.open.model.OpenTabRequest;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class BarDetailCheckinPresenter extends BasePresenter<OpenTabEvent, Observable<CheckInResponse>> implements CacheInterface {
    private CheckInInteractor checkInInteractor;

    public BarDetailCheckinPresenter(BaseActivity activity) {
        super(activity);
        checkInInteractor = new CheckInInteractor(getActivity());
    }

    @Override public Observable<CheckInResponse> process(OpenTabEvent open) {
        OpenTabRequest request = new OpenTabRequest();
        request.setIgnoreAnotherCheckIns(open.isIgnoreAnotherCheckIns());
        CheckInRequest checkInRequest = new CheckInRequest(open.getCurrentBarId());
        request.setCheckinRequest(checkInRequest);
        request.setIgnoreBluetoothState(open.isIgnoreBluetoothState());
        return checkInInteractor.process(request);
    }

    @Override
    public boolean saveToCache(long val) {
        return QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, val);
    }
}
