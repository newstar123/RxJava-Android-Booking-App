package app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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


public class ShowMarkersOnMapFromBarSubBinder {
    private BaseActivity activity;
    private ArrayList<Marker> markers;

    public ShowMarkersOnMapFromBarSubBinder(BaseActivity activity) {
        this.activity = activity;
        markers = new ArrayList<>();
    }

    public void apply(MapView mapView, List<LatLng> route) {
        MapObservable.get(mapView).subscribe(map -> apply(map, route), err -> {});
    }

    public void apply(GoogleMap map, List<LatLng> route) {
        Observable.from(markers).subscribe(Marker::remove);
        Bitmap addressPointBitmap = ShowMarkersOnMapToBarSubBinder.getBitmapFromVectorDrawable(activity, R.drawable.inset_order_ride_to_point);
        BitmapDescriptor fromPoint = BitmapDescriptorFactory.fromBitmap(addressPointBitmap);
        LatLng fromLatLng =route.get(route.size() - 1);
        MarkerOptions fromPointMarkerOption = new MarkerOptions().icon(fromPoint).position(fromLatLng)
                .anchor(0.5f, 0.5f);
        markers.add(map.addMarker(fromPointMarkerOption));
        Bitmap destinationPointBitmap = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.order_ride_bar_point);
        BitmapDescriptor destinationPoint = BitmapDescriptorFactory.fromBitmap(destinationPointBitmap);
        LatLng destinationLatLng = route.get(0);
        MarkerOptions destinationPointMarkerOption = new MarkerOptions().position(destinationLatLng).icon(destinationPoint)
                .anchor(0.5f, 0.5f);
        markers.add(map.addMarker(destinationPointMarkerOption));
    }

    public void clear() {
        Observable.from(markers).subscribe(Marker::remove);
    }
}
