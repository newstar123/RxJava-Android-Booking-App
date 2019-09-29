package app.core.uber.mock.ride.gateway;


import app.core.uber.mock.ride.entity.UberReceiptResponse;
import rx.Observable;

public interface UberReceiptGateway {
    Observable<UberReceiptResponse> get(String uberRideId);
}
