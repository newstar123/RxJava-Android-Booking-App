package app.gateway.uber.fare;

import com.google.android.gms.maps.model.LatLng;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;

import app.core.uber.auth.entity.UberAuthToken;
import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fare.gateway.PostUberEstimateGateway;
import app.core.uber.fares.entity.PostUberEstimateResponse;
import app.core.uber.product.entity.UberProductResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;
import app.gateway.rest.client.UberHttpClient;
import app.gateway.uber.auth.GetUberAuthTokenSdkGateway;
import app.gateway.uber.fares.PostUberEstimatesMainGateway;
import rx.Observable;
import rx.schedulers.Schedulers;


public class PostUberEstimateRestGateway implements PostUberEstimateGateway {
    private final GetUberAuthTokenSdkGateway getUberAuthTokenGateway;
    private final PostUberEstimateRetrofitGateway postUberEstimateRetrofitGateway;

    public PostUberEstimateRestGateway(BaseActivity activity) {
        getUberAuthTokenGateway = new GetUberAuthTokenSdkGateway(activity);
        postUberEstimateRetrofitGateway = UberHttpClient.get().create(PostUberEstimateRetrofitGateway.class);
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
        String token = uberAuthToken.getSession()
                .getAuthenticator()
                .getTokenStorage()
                .getAccessToken()
                .getToken();
        String tokenWithPrefix = UberHttpClient.createTokenWithPrefix(token);
        RideRequestParameters build = createRequestParams(request);
        return postUberEstimateRetrofitGateway.get(tokenWithPrefix , build)
                .map(result -> createResponse(result, request));
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
