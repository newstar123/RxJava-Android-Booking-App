package app.core.uber.start.interactor;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import app.core.BaseInteractor;
import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fare.gateway.PostUberEstimateGateway;
import app.core.uber.product.entity.UberProductsResponse;
import app.core.uber.product.gateway.GetUberProductsGateway;
import app.core.uber.start.entity.ApplyRidePromoRequest;
import app.core.uber.start.entity.ApplyRidePromoResponse;
import app.core.uber.start.entity.CheckRidePromoRequest;
import app.core.uber.start.entity.RegisterRideRequest;
import app.core.uber.start.entity.RegisterRideResponse;
import app.core.uber.start.entity.RideDirection;
import app.core.uber.start.entity.RideRequestInvalidException;
import app.core.uber.start.entity.StartUberRideRequest;
import app.core.uber.start.entity.StartUberRideResponse;
import app.core.uber.start.gateway.ApplyRidePromoGateway;
import app.core.uber.start.gateway.CheckRidePromoGateway;
import app.core.uber.start.gateway.RegisterRideGateway;
import app.core.uber.start.gateway.StartUberRideGateway;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.ride.promo.apply.ApplyRidePromoRestGateway;
import app.gateway.ride.promo.check.CheckRidePromoRestGateway;
import app.gateway.ride.register.RegisterRideRestGateway;
import app.gateway.uber.fare.PostUberEstimateRestGateway;
import app.gateway.uber.product.GetUberProductsSdkGateway;
import app.gateway.uber.start.StartUberRideSdkGateway;
import rx.Observable;

public class StartUberRideInteractor implements BaseInteractor<StartUberRideRequest, Observable<StartUberRideResponse>> {
    private final CheckNetworkPermissionGateway checkNetworkPermissionGateway;
    private final StartUberRideGateway startUberRideGateway;
    private final RegisterRideGateway registerRideGateway;
    private final CheckRidePromoGateway checkRidePromoGateway;
    private final ApplyRidePromoGateway applyRidePromoGateway;
    private final PostUberEstimateGateway postUberEstimateGateway;
    private final GetUberProductsGateway getUberProductsGateway;

    public StartUberRideInteractor(BaseActivity activity) {
        checkNetworkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        startUberRideGateway = new StartUberRideSdkGateway(activity);
        registerRideGateway = new RegisterRideRestGateway(activity);
        checkRidePromoGateway = new CheckRidePromoRestGateway(activity);
        applyRidePromoGateway = new ApplyRidePromoRestGateway(activity);
        postUberEstimateGateway = new PostUberEstimateRestGateway(activity);
        getUberProductsGateway = new GetUberProductsSdkGateway(activity);
    }

    @Override public Observable<StartUberRideResponse> process(StartUberRideRequest startUberRideRequest) {
        return checkNetworkPermissionGateway.check()
                .concatMap(isGranted -> selectFlowForFareId(startUberRideRequest))
                .concatMap(isGranted -> startUberRideGateway.post(startUberRideRequest))
                .concatMap(this::cacheUberRideId)
                .onErrorResumeNext(throwable -> Observable.error(new RideRequestInvalidException(throwable)))
                .concatMap(startUberResponse -> selectFlowForRideDirection(startUberResponse, startUberRideRequest));
    }

    private Observable<StartUberRideRequest> selectFlowForFareId(StartUberRideRequest request) {
        if (TextUtils.isEmpty(request.getFareId()))
            return getFareIdFlow(request);
        else
            return Observable.just(request);
    }

    private Observable<StartUberRideRequest> getFareIdFlow(StartUberRideRequest request) {
        PostUberEstimateRequest estimateRequest = new PostUberEstimateRequest();
        estimateRequest.setCapacity(request.getCapacity());
        estimateRequest.setDeparture(request.getDeparture());
        estimateRequest.setDestination(request.getDestination());
        return getUberProductsGateway.get(request.getDeparture())
                .flatMapIterable(UberProductsResponse::getProducts)
                .filter(product -> product.getProductId().equals(request.getProductId()))
                .first()
                .doOnNext(estimateRequest::setProduct)
                .concatMap(product -> postUberEstimateGateway.post(estimateRequest))
                .doOnNext(estimate -> request.setFareId(estimate.getRideEstimate().getFare().getFareId()))
                .concatMap(estimate -> Observable.just(request));

    }

    private Observable<StartUberRideResponse> cacheUberRideId(StartUberRideResponse response) {
            return Observable.just(QorumSharedCache.checkUberRideId().save(BaseCacheType.STRING, response.getBody().getRideId()))
                    .map(id -> response);
    }

    private Observable<StartUberRideResponse> selectFlowForRideDirection(StartUberRideResponse response,
                                                                         StartUberRideRequest request) {
        RideDirection rideDirection = request.getRideDirection();
        if (rideDirection == RideDirection.FROM_BAR && request.getDiscount() > 0)
            return registerRide(response, request)
                    .concatMap(this::checkRidePromo)
                    .concatMap(this::applyRidePromo)
                    .concatMap(result -> Observable.just(response));
        else if (rideDirection == RideDirection.TO_BAR)
            return registerRide(response, request)
                    .concatMap(registerResponse -> Observable.just(response));
        else
            return registerRide(response, request)
                    .map(registerResponse -> response);
    }

    private Observable<ApplyRidePromoResponse> applyRidePromo(RegisterRideResponse response) {
        long rideId = response.getData().getId();
        return Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(checkInId -> (long) checkInId)
                .map(ApplyRidePromoRequest::new)
                .doOnNext(applyRidePromoRequest -> applyRidePromoRequest.setRideId(rideId))
                .concatMap(applyRidePromoGateway::put);
    }

    private Observable<RegisterRideResponse> checkRidePromo(RegisterRideResponse registerRideResponse) {
        return Observable.just(QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(checkInId -> (long) checkInId)
                .concatMap(checkinId -> checkRidePromoGateway.get(new CheckRidePromoRequest(checkinId)))
                .map(result -> registerRideResponse);
    }

    private Observable<RegisterRideResponse> registerRide(StartUberRideResponse startUberResponse, StartUberRideRequest startUberRideRequest) {
        RegisterRideRequest request = new RegisterRideRequest();
        request.setDestinationAddress(startUberRideRequest.getDestinationAddress());
        request.setStartAddress(startUberRideRequest.getDepartureAddress());
        LatLng destination = startUberRideRequest.getDestination();
        request.setDestinationLatitude(destination.latitude);
        request.setDestinationLongitude(destination.longitude);
        LatLng departure = startUberRideRequest.getDeparture();
        request.setStartLatitude(departure.latitude);
        request.setStartLongitude(departure.longitude);
        request.setRequestId(startUberResponse.getBody().getRideId());
        request.setRideType(startUberRideRequest.getRideDirection());
        return registerRideGateway.post(request);
    }
}
