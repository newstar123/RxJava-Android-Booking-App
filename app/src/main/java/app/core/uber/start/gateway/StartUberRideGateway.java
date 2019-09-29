package app.core.uber.start.gateway;


import app.core.uber.start.entity.StartUberRideRequest;
import app.core.uber.start.entity.StartUberRideResponse;
import rx.Observable;

public interface StartUberRideGateway {
    Observable<StartUberRideResponse> post(StartUberRideRequest request);
}
