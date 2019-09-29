package app.core.uber.mock.ride.interactor;


import android.content.Context;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.core.BaseInteractor;
import app.core.uber.mock.ride.entity.UberMockRideRequest;
import app.core.uber.mock.ride.entity.UberReceiptResponse;
import app.core.uber.mock.ride.gateway.UberMockRideGateway;
import app.gateway.uber.mock.UberMockRideSdkGateway;
import rx.Observable;

public class UberMockRideInteractor implements BaseInteractor<String, Observable<UberReceiptResponse>> {
    private final List<String> rideStatuses = Arrays.asList("accepted", "arriving", "in_progress", "completed");
    private final UberMockRideGateway uberMockRideGateway;

    public UberMockRideInteractor(Context context) {
        uberMockRideGateway = new UberMockRideSdkGateway();
    }

    @Override public Observable<UberReceiptResponse> process(String uberRideId) {
        return iterateThroughRideStatuses(uberRideId)
                .filter("completed"::equals)
                .map(status -> new UberReceiptResponse());
    }

    private Observable<String> iterateThroughRideStatuses(String uberRideId) {
       return Observable.from(rideStatuses)
                .concatMap(status -> change(status, uberRideId));
    }

    private Observable<String> change(String status, String uberRideId) {
        UberMockRideRequest request = new UberMockRideRequest();
        request.setStatus(status);
        request.setUberRideId(uberRideId);
        return uberMockRideGateway.get(request)
                .retryWhen(observable -> Observable.timer(5, TimeUnit.SECONDS))
                .map(result -> status);
    }
}
