package app.gateway.uber.fare;

import com.google.android.gms.maps.model.LatLng;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

import app.core.uber.auth.entity.UberAuthToken;
import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fare.gateway.PostUberEstimateGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.core.uber.product.entity.UberProductResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;
import app.gateway.uber.auth.GetUberAuthTokenSdkGateway;
import app.gateway.uber.fares.PostUberEstimatesMainGateway;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class PostUberEstimateSdkGateway implements PostUberEstimateGateway {
    private final GetUberAuthTokenSdkGateway getUberAuthTokenGateway;

    public PostUberEstimateSdkGateway(BaseActivity activity) {
        getUberAuthTokenGateway = new GetUberAuthTokenSdkGateway(activity);
    }

    @Override public Observable<PostUberEstimateResponse> post(PostUberEstimateRequest request) {
        return getUberAuthTokenGateway.get()
                .concatMap(uberAuthToken -> doRequest(uberAuthToken, request));
    }

    private Observable<PostUberEstimateResponse> doRequest(UberAuthToken uberAuthToken, PostUberEstimateRequest estimateRequest) {
        return Observable
                .just(estimateRequest)
                .subscribeOn(Schedulers.from(PostUberEstimatesMainGateway.executorService))
                .observeOn(Schedulers.from(PostUberEstimatesMainGateway.executorService))
                .concatMap(request -> getFareForProduct(uberAuthToken, request));
    }

    private Observable<PostUberEstimateResponse> getFareForProduct(UberAuthToken uberAuthToken,
                                                                   PostUberEstimateRequest request) {
        return Observable.create(new Observable.OnSubscribe<PostUberEstimateResponse>() {
            @Override public void call(Subscriber<? super PostUberEstimateResponse> subscriber) {
                RidesService service = UberRidesApi.with(uberAuthToken.getSession()).build().createService();
                RideRequestParameters build = createRequestParams(request);
                Call<RideEstimate> rideEstimateCall = service.estimateRide(build);
                try {
                    Response<RideEstimate> execute = rideEstimateCall.execute();
                    if (!execute.isSuccessful())
                        subscriber.onError(new RuntimeException(execute.errorBody().string()));
                    RideEstimate body = execute.body();
                    PostUberEstimateResponse postUberEstimateResponse = createResponse(body, request);
                    subscriber.onNext(postUberEstimateResponse);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private RideRequestParameters createRequestParams(PostUberEstimateRequest request) {
        LatLng departure = request.getDeparture();
        LatLng destination = request.getDestination();
        RideRequestParameters.Builder builder = new RideRequestParameters.Builder();
        UberProductResponse product = request.getProduct();
        if (product.getDisplayName().equals(RideCategoryFactory.POOL) && request.getCapacity() <= 0)
            builder = builder.setSeatCount(1);
        if (request.getCapacity() > 0 && product.getDisplayName().equals(RideCategoryFactory.POOL))
            builder = builder.setSeatCount(request.getCapacity());
        return builder.setProductId(product.getProductId())
                .setPickupCoordinates((float) departure.latitude, (float) departure.longitude)
                .setDropoffCoordinates((float) destination.latitude, (float) destination.longitude)
                .build();
    }

    private PostUberEstimateResponse createResponse(RideEstimate body, PostUberEstimateRequest request) {
        PostUberEstimateResponse postUberEstimateResponse = new PostUberEstimateResponse();
        postUberEstimateResponse.setRideEstimate(body);
        postUberEstimateResponse.setTimestamp(System.currentTimeMillis());
        postUberEstimateResponse.setDeparture(request.getDeparture());
        postUberEstimateResponse.setDestination(request.getDestination());
        postUberEstimateResponse.setProduct(request.getProduct());
        return postUberEstimateResponse;
    }


}
