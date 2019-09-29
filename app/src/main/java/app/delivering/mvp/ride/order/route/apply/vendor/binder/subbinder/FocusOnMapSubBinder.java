package app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;

public class FocusOnMapSubBinder {
    private BaseActivity activity;

    public FocusOnMapSubBinder(BaseActivity activity) {
        this.activity = activity;
    }

    public void apply(MapView mapView, List<LatLng> route){
        MapObservable.get(mapView).subscribe(map -> apply(map, route), Throwable::printStackTrace);
    }

    public void apply(GoogleMap map, List<LatLng> route) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(route.get(0));
        builder.include(route.get(route.size() - 1));
        LatLngBounds bounds = builder.build();
        int padding = activity.getResources().getDimensionPixelOffset(R.dimen.dip16);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu, 1000, null);
    }
}
