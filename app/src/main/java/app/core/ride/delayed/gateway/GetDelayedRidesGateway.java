package app.core.ride.delayed.gateway;

import java.util.List;

import app.core.checkin.user.get.entity.GetCheckInsResponse;
import rx.Observable;

public interface GetDelayedRidesGateway {
    Observable<List<GetCheckInsResponse>> get();
}
