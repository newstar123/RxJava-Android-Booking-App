package app.gateway.uber.mock;

import com.uber.sdk.rides.client.model.SandboxRideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

import app.core.payment.regular.model.EmptyResponse;
import app.core.uber.mock.ride.entity.UberMockRideRequest;
import app.core.uber.mock.ride.gateway.UberMockRideGateway;
import app.gateway.uber.UberSdkRequestSuccess;
import app.gateway.uber.UberSdkServiceWithoutTokenCheck;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;


public class UberMockRideSdkGateway implements UberMockRideGateway {

    @Override public Observable<EmptyResponse> get(UberMockRideRequest request) {
        Call<Void> requestCall = createRequest(request);
        return Observable.create((Observable.OnSubscribe<Response<Void>>) subscriber -> {
            try {
                Response<Void> execute = requestCall.execute();
                subscriber.onNext(execute);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io())
                .doOnNext(UberSdkRequestSuccess::check)
                .map(result -> new EmptyResponse());
    }

    private Call<Void> createRequest(UberMockRideRequest request) {
        RidesService service = UberSdkServiceWithoutTokenCheck.create();
        SandboxRideRequestParameters build = new SandboxRideRequestParameters.Builder()
                .setStatus(request.getStatus())
                .build();
        String uberRideId = request.getUberRideId();
        return service.updateSandboxRide(uberRideId, build);
    }
}
