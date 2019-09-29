package app.core.uber.estimate.price.interactor;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import app.core.BaseInteractor;
import app.core.location.get.GetRxLocationInteractor;
import app.core.uber.estimate.price.entity.UberPriceEstimatesResponse;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.uber.estimate.price.GetUberPriceEstimateRestGateway;
import rx.Observable;

public class GetUberEstimatesInteractor implements BaseInteractor<LatLng, Observable<UberPriceEstimatesResponse>> {
    private GetUberPriceEstimateRestGateway estimateRestGateway;
    private GetRxLocationInteractor rxLocationInteractor;
    private CheckNetworkPermissionGateway networkPermissionGateway;

    public GetUberEstimatesInteractor(BaseActivity activity){
        rxLocationInteractor = new GetRxLocationInteractor(activity);
        estimateRestGateway = new GetUberPriceEstimateRestGateway(activity);
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
    }

    @Override public Observable<UberPriceEstimatesResponse> process(LatLng barPosition) {
        return networkPermissionGateway.check()
                .concatMap(isOk -> rxLocationInteractor.process())
                .concatMap(this::toLatLng)
                .concatMap(userPosition -> estimateRestGateway.get(userPosition, barPosition));
    }

    private Observable<LatLng> toLatLng(Location location) {
        return Observable.create(subscriber -> {
            LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
            subscriber.onNext(userPosition);
            subscriber.onCompleted();
        });
    }
}
