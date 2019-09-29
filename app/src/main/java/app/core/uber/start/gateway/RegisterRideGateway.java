package app.core.uber.start.gateway;


import app.core.uber.start.entity.RegisterRideRequest;
import app.core.uber.start.entity.RegisterRideResponse;
import rx.Observable;

public interface RegisterRideGateway {
    Observable<RegisterRideResponse> post(RegisterRideRequest request);
}
