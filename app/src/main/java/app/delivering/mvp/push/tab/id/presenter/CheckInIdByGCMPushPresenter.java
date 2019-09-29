package app.delivering.mvp.push.tab.id.presenter;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import app.core.checkin.item.interactor.GetCheckInByIdInteractor;
import app.core.checkin.item.interactor.GetCheckInInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.mvp.BaseContextPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CheckInIdByGCMPushPresenter extends BaseContextPresenter<JSONObject, Observable<CheckInResponse>> {
    private static final String CHECKIN_ID = "checkin_id";
    private final GetCheckInInteractor checkInByIdInteractor;

    public CheckInIdByGCMPushPresenter(Context context) {
        super(context);
        checkInByIdInteractor = new GetCheckInByIdInteractor(context);
    }

    @Override public Observable<CheckInResponse> process(JSONObject json) {
        return Observable.just(json)
                .map(jsonObject -> {
                    long checkInId = 0;
                    try {
                        checkInId = json.getLong(CHECKIN_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return checkInId;
                })
                .filter(id -> id > 0)
                .concatMap(checkInByIdInteractor::process)
                .concatMap(this::checkTicketState);
    }

    private Observable<CheckInResponse> checkTicketState(CheckInResponse checkInResponse) {
        if (checkInResponse.getCheckin().getCheckoutTime() == null)
            return Observable.just(checkInResponse);
        else
            return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, 0))
                    .map(emptyId -> QorumSharedCache.checkTimeLeftToRide().save(BaseCacheType.INT, 0))
                    .map(emptyId -> QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0))
                    .map(emptyId -> QorumSharedCache.checkUberRideId().save(BaseCacheType.STRING, ""))
                    .map(emptyId -> QorumSharedCache.checkCheckoutId().save(BaseCacheType.LONG, checkInResponse.getCheckin().getId()))
                    .map(emptyId -> QorumSharedCache.checkFreeRideWarning().save(BaseCacheType.BOOLEAN,false))
                    .concatMap(emptyId -> Observable.just(checkInResponse));
    }
}
