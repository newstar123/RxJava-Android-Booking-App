package app.core.checkin.active;

import app.core.BaseInteractor;
import app.core.bars.detail.interactor.GetItemBarInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.main.service.checkin.model.CheckActiveCheckInResult;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CheckActiveCheckInsInteractor implements BaseInteractor<GetCheckInsResponse, Observable<CheckActiveCheckInResult>> {
    private GetItemBarInteractor getItemBarInteractor;

    public CheckActiveCheckInsInteractor(BaseActivity activity) {
        getItemBarInteractor = new GetItemBarInteractor(activity);
    }

    @Override public Observable<CheckActiveCheckInResult> process(GetCheckInsResponse activeCheckIn) {
        return loadBarInformation(activeCheckIn)
                .concatMap(this::saveCheckinIDs);
    }

    private Observable<CheckActiveCheckInResult> loadBarInformation(GetCheckInsResponse response) {
        return getItemBarInteractor.process((long)response.getVendorId())
                .concatMap(barModel -> updateResult(barModel, response));
    }

    private Observable<CheckActiveCheckInResult> updateResult(BarModel barModel, GetCheckInsResponse response) {
        return Observable.create(subscriber -> {
            CheckActiveCheckInResult result = new CheckActiveCheckInResult();
            result.setBarId(response.getVendorId());
            result.setActiveCheckInId(response.getId());
            result.setBarName(barModel.getName());
            result.setBarImage(barModel.getBackgroundImageUrl());
            subscriber.onNext(result);
            subscriber.onCompleted();
        });
    }

    private Observable<CheckActiveCheckInResult> saveCheckinIDs(CheckActiveCheckInResult result) {
        return Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, result.getBarId()))
                .map(barId -> QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, result.getActiveCheckInId()))
                .concatMap(checkInId -> Observable.just(result));
    }
}
