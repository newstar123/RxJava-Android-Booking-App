package app.gateway.uber.start;

import com.google.android.gms.maps.model.LatLng;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

import app.core.uber.auth.entity.UberAuthToken;
import app.core.uber.start.entity.StartUberRideRequest;
import app.core.uber.start.entity.StartUberRideResponse;
import app.core.uber.start.gateway.StartUberRideGateway;
import app.delivering.component.BaseActivity;
import app.gateway.uber.auth.GetUberAuthTokenSdkGateway;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class StartUberRideSdkGateway implements StartUberRideGateway {
    private final GetUberAuthTokenSdkGateway getUberAuthTokenGateway;

    public StartUberRideSdkGateway(BaseActivity activity) {
        getUberAuthTokenGateway = new GetUberAuthTokenSdkGateway(activity);
    }

    @Override public Observable<StartUberRideResponse> post(StartUberRideRequest request) {
        return getUberAuthTokenGateway.get()
                .concatMap(uberAuthToken -> doRequest(uberAuthToken, request));
    }

    private Observable<StartUberRideResponse> doRequest(UberAuthToken uberAuthToken,
                                                        StartUberRideRequest request) {
        RidesService service = UberRidesApi.with(uberAuthToken.getSession()).build().createService();
        LatLng departure = request.getDeparture();
        LatLng destination = request.getDestination();
        RideRequestParameters build = new RideRequestParameters.Builder()//
                .setFareId(request.getFareId())
                .setProductId(request.getProductId())
                .setPickupAddress(request.getDepartureAddress())
                .setDropoffAddress(request.getDestinationAddress())
                .setPickupCoordinates((float)departure.latitude, (float)departure.longitude)
                .setDropoffCoordinates((float) destination.latitude, (float) destination.longitude)
                .build();
        Call<Ride> rideCall = service.requestRide(build);
        return Observable.create(new Observable.OnSubscribe<StartUberRideResponse>() {
            @Override public void call(Subscriber<? super StartUberRideResponse> subscriber) {
                try {
                    Response<Ride> execute = rideCall.execute();
                    if (!execute.isSuccessful())
                        subscriber.onError(new RuntimeException(execute.errorBody().string()));
                    Ride body = execute.body();
                    subscriber.onNext(new StartUberRideResponse(body));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
