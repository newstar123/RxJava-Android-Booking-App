package app.gateway.direction;


import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;

import app.R;
import app.core.location.route.entity.RoutePlaceByIdToBarRequest;
import app.core.location.route.gateway.RouteGateway;
import app.delivering.component.BaseActivity;
import rx.Observable;
import rx.schedulers.Schedulers;


public class GoogleRouteGateway implements RouteGateway {
    private final GeoApiContext context;

    public GoogleRouteGateway(BaseActivity activity) {
       context = new GeoApiContext().setApiKey(activity.getResources().getString(R.string.geo_api_key));
    }

    @Override public Observable<DirectionsResult> get(RoutePlaceByIdToBarRequest request) {
        final double departureLatitude = request.getDeparture().latitude;
        final double departureLongitude = request.getDeparture().longitude;
        final double destinationLatitude = request.getDestination().latitude;
        final double destinationLongitude = request.getDestination().longitude;

        LatLng adoptedDeparture = new LatLng(departureLatitude, departureLongitude);
        LatLng adoptedDestination = new LatLng(destinationLatitude, destinationLongitude);

        return Observable.create((Observable.OnSubscribe<DirectionsResult>) subscriber -> {
            try {
                DirectionsApiRequest directions = DirectionsApi.newRequest(context);
                directions.alternatives(false);
                directions.origin(adoptedDeparture);
                directions.destination(adoptedDestination);
                directions.setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                });
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }
}
