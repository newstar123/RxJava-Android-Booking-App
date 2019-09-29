package app.core.uber.start.gateway;


import app.core.uber.start.entity.ApplyRidePromoRequest;
import app.core.uber.start.entity.ApplyRidePromoResponse;
import rx.Observable;

public interface ApplyRidePromoGateway {
    Observable<ApplyRidePromoResponse> put(ApplyRidePromoRequest request);
}
