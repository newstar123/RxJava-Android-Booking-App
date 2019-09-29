package app.delivering.mvp.ride.order.route.apply.vendor.binder.map;


import android.content.res.Resources;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.MapStyleOptions;

import app.R;
import rx.Observable;

public class MapObservable {

    public static Observable<GoogleMap> get(MapView mapView) {
        return Observable.create(subscriber ->
                mapView.getMapAsync(googleMap -> {
                            try {
                                MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(mapView.getContext(), R.raw.map_style);
                                googleMap.setMapStyle(mapStyleOptions);
                                googleMap.getUiSettings().setMapToolbarEnabled(false);
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                    subscriber.onNext(googleMap);
                    subscriber.onCompleted();
                }));
    }
}
