package app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;

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
import app.delivering.mvp.ride.order.init.frombar.binder.FromBarInitBinder;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import rx.Observable;


public class ShowMarkersOnMapToBarSubBinder {
    private BaseActivity activity;
    private ArrayList<Marker> markers;

    public ShowMarkersOnMapToBarSubBinder(BaseActivity activity) {
        this.activity = activity;
        markers = new ArrayList<>();
    }

    public void apply(MapView mapView, List<LatLng> route) {
        MapObservable.get(mapView).subscribe(map -> apply(map, route), err -> {});
    }

    public void apply(GoogleMap map, List<LatLng> route) {
        Observable.from(markers).subscribe(Marker::remove);
        Bitmap addressPointBitmap = createAddressPoint();
        BitmapDescriptor fromPoint = BitmapDescriptorFactory.fromBitmap(addressPointBitmap);
        LatLng fromLatLng = route.get(0);
        MarkerOptions fromPointMarkerOption = new MarkerOptions().icon(fromPoint).position(fromLatLng)
                .anchor(0.5f, 0.5f);
        markers.add(map.addMarker(fromPointMarkerOption));
        Bitmap destinationPointBitmap = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.order_ride_bar_point);
        BitmapDescriptor destinationPoint = BitmapDescriptorFactory.fromBitmap(destinationPointBitmap);
        LatLng destinationLatLng = route.get(route.size() - 1);
        MarkerOptions destinationPointMarkerOption = new MarkerOptions().position(destinationLatLng).icon(destinationPoint)
                .anchor(0.5f, 0.5f);
        markers.add(map.addMarker(destinationPointMarkerOption));
    }

    private Bitmap createAddressPoint() {
        if (FromBarInitBinder.getRideType(activity) == InitialRideType.FROM_THE_VENUE)
            return getBitmapFromVectorDrawable(activity, R.drawable.inset_order_ride_to_point);
        else
            return getBitmapFromVectorDrawable(activity, R.drawable.inset_order_ride_from_point);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void clear() {
        Observable.from(markers).subscribe(Marker::remove);
    }
}
