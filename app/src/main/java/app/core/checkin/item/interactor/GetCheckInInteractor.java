package app.core.checkin.item.interactor;

import app.core.BaseInteractor;
import app.core.checkin.user.post.entity.CheckInResponse;
import rx.Observable;

public interface GetCheckInInteractor extends BaseInteractor<Long, Observable<CheckInResponse>> {

    Observable<CheckInResponse> forceLoad(long checkInId);

}
