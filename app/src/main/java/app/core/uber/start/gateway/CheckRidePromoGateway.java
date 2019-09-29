package app.core.uber.start.gateway;


import app.core.uber.start.entity.CheckRidePromoRequest;
import app.core.uber.start.entity.CheckRidePromoResponse;
import rx.Observable;

public interface CheckRidePromoGateway {
    Observable<CheckRidePromoResponse> get(CheckRidePromoRequest request);
}
