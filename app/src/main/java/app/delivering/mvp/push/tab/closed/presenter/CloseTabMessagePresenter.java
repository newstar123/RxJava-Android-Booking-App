package app.delivering.mvp.push.tab.closed.presenter;

import android.content.Context;

import org.json.JSONObject;

import app.core.checkin.item.interactor.GetCheckInByIdInteractor;
import app.core.checkin.item.interactor.GetCheckInInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.mvp.BaseContextPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CloseTabMessagePresenter extends BaseContextPresenter<JSONObject, Observable<CheckInResponse>>{
    private static final String CHECKIN_ID = "checkin_id";
    private final GetCheckInInteractor getCheckInByIdInteractor;
    private Long currentCheckInId;

    public CloseTabMessagePresenter(Context context) {
        super(context);
        getCheckInByIdInteractor = new GetCheckInByIdInteractor(context);
    }

    @Override public Observable<CheckInResponse> process(JSONObject json) {
        return getCheckOutTabID(json)
                .doOnNext(currCheckInId -> this.currentCheckInId = currCheckInId)
                .concatMap(currCheckInId -> QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(cacheVal -> (long) cacheVal)
                .filter(cacheCheckInId -> cacheCheckInId.equals(currentCheckInId))
                .concatMap(cacheCheckInId -> clearBarIdCache(currentCheckInId))
                .concatMap(cacheCheckInId -> getCheckInByIdInteractor.process(currentCheckInId))
                .concatMap(this::clearCache);
    }

    private Observable<CheckInResponse> clearCache(CheckInResponse model) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, 0))
                .map(checkinBarId -> QorumSharedCache.checkCheckoutId().save(BaseCacheType.LONG, model.getCheckin().getId()))
                .map(checkinBarId -> model)
                .doOnNext(checkInResponse -> QorumSharedCache.checkFreeRideDialogAlreadyShown().save(BaseCacheType.BOOLEAN, false));
    }

    private Observable<Long> clearBarIdCache(Long checkinId) {
        return Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0))
                .map(emptyId -> checkinId);
    }

    private Observable<Long> getCheckOutTabID(JSONObject json) {
        return Observable.create(subscriber -> {
            try {
                long id = json.getLong(CHECKIN_ID);
                subscriber.onNext(id);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
