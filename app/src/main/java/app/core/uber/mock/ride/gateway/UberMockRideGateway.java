package app.core.uber.mock.ride.gateway;


import app.core.payment.regular.model.EmptyResponse;
import app.core.uber.mock.ride.entity.UberMockRideRequest;
import rx.Observable;

public interface UberMockRideGateway {
    Observable<EmptyResponse> get(UberMockRideRequest request);
}
