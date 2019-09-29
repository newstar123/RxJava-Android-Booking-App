package app.delivering.mvp.ride.order.type.select.binder.subbinder;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.PolylineAnimator;

public class RouteOnMapSubBinder {
    private BaseActivity activity;

    public RouteOnMapSubBinder(BaseActivity activity) {
        this.activity = activity;
    }

    public void apply(MapView mapView, List<LatLng> route){
        MapObservable.get(mapView).subscribe(map -> apply(map, route), err -> {});
    }

    public void apply(GoogleMap map, List<LatLng> route) {
        PolylineAnimator.getInstance().stop();
        final float polylineSize = activity.getResources().getDimension(R.dimen.dip4);

        PolylineOptions optionsBackground = new PolylineOptions()
                .addAll(route)
                .color(PolylineAnimator.GREY)
                .width(polylineSize);
        PolylineAnimator.getInstance().setOptionsBackground(optionsBackground);

        PolylineOptions optionsForeground = new PolylineOptions()
                .addAll(route)
                .color(PolylineAnimator.BLUE)
                .width(polylineSize);
        
        PolylineAnimator.getInstance().setOptionsForeground(optionsForeground);
        PolylineAnimator.getInstance().animate(map, route);
    }
}
