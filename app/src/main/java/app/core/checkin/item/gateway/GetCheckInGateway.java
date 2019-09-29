package app.core.checkin.item.gateway;

import app.core.checkin.user.post.entity.CheckInResponse;
import rx.Observable;

public interface GetCheckInGateway {
    Observable<CheckInResponse> get(long checkInId, boolean isForceRequest);
}
