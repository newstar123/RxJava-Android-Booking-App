package app.core.checkin.user.post.gateway;

import app.core.checkin.user.post.entity.CheckInRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import rx.Observable;

public interface PostCheckInGateway {
    Observable<CheckInResponse> post(CheckInRequest checkInRequest);
}
