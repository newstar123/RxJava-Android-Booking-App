package app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import rx.Observable;


public class ShowMarkersOnMapDeferredRideSubBinder {
    private BaseActivity activity;
    private ArrayList<Marker> markers;

    public ShowMarkersOnMapDeferredRideSubBinder(BaseActivity activity) {
        this.activity = activity;
        markers = new ArrayList<>();
    }

    public void apply(MapView mapView, List<LatLng> route) {
        MapObservable.get(mapView).subscribe(map -> apply(map, route), onErr -> {});
    }

    public void apply(GoogleMap map, List<LatLng> route) {
        Observable.from(markers).subscribe(Marker::remove);
        addDeparture(map, route.get(0));
        addDestination(map, route.get(route.size() - 1));
    }

    public void addDeparture(MapView mapView, LatLng point) {
        MapObservable.get(mapView).subscribe(map -> addDeparture(map, point), err -> {});
    }

    public void addDeparture(GoogleMap map, LatLng point) {
        Bitmap departurePointBitmap = ShowMarkersOnMapToBarSubBinder.getBitmapFromVectorDrawable(activity, R.drawable.inset_order_ride_from_point);
        BitmapDescriptor departurePoint = BitmapDescriptorFactory.fromBitmap(departurePointBitmap);
        MarkerOptions departurePointMarkerOption = new MarkerOptions().position(point).icon(departurePoint)
                .anchor(0.5f, 0.5f);
        markers.add(map.addMarker(departurePointMarkerOption));
    }

    public void addDestination(MapView mapView, LatLng point) {
        MapObservable.get(mapView).subscribe(map -> addDestination(map, point));
    }

    public void addDestination(GoogleMap map, LatLng point) {
        Bitmap destinationPointBitmap = ShowMarkersOnMapToBarSubBinder.getBitmapFromVectorDrawable(activity, R.drawable.inset_order_ride_to_point);
        BitmapDescriptor destinationPoint = BitmapDescriptorFactory.fromBitmap(destinationPointBitmap);
        MarkerOptions destinationPointMarkerOption = new MarkerOptions().position(point).icon(destinationPoint)
                .anchor(0.5f, 0.5f);
        markers.add(map.addMarker(destinationPointMarkerOption));
    }

    public void clear() {
        Observable.from(markers).subscribe(Marker::remove);
    }
}
