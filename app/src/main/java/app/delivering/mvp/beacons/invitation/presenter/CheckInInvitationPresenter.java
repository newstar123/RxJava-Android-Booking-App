package app.delivering.mvp.beacons.invitation.presenter;

import android.content.Context;

import app.delivering.mvp.BaseContextPresenter;
import app.delivering.mvp.beacons.invitation.events.CheckInInvitationEvent;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CheckInInvitationPresenter extends BaseContextPresenter<CheckInInvitationEvent, Observable<CheckInInvitationEvent>> {

    public CheckInInvitationPresenter(Context context) {
        super(context);
    }

    @Override
    public Observable<CheckInInvitationEvent> process(CheckInInvitationEvent checkInInvitationEvent) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .filter(id -> (long) id == 0)
                .map(emptyCheckInId -> checkInInvitationEvent);
    }
}
