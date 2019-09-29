package app.delivering.mvp.push.tab.tiket.presenter;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import app.core.checkin.item.interactor.GetCheckInByIdInteractor;
import app.core.checkin.item.interactor.GetCheckInInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.mvp.BaseContextPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CloseEmptyTicketPresenter extends BaseContextPresenter<JSONObject, Observable<CheckInResponse>> {
    private static final String CHECKIN_ID = "checkin_id";
    private final GetCheckInInteractor getCheckInByIdInteractor;

    public CloseEmptyTicketPresenter(Context context) {
        super(context);
        getCheckInByIdInteractor = new GetCheckInByIdInteractor(context);
    }

    @Override
    public Observable<CheckInResponse> process(JSONObject jsonObject) {
        return Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0))
                .map(val -> QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, 0))
                .concatMap(aLong -> getCheckInId(jsonObject))
                .doOnNext(aLong -> Log.d("NO_TICKET", "ticket-" + aLong))
                .concatMap(getCheckInByIdInteractor::process);
    }

    private Observable<Long> getCheckInId(JSONObject json) {
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
