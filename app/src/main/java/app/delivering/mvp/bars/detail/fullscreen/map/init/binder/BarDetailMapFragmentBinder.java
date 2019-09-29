package app.delivering.mvp.bars.detail.fullscreen.map.init.binder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.delivering.component.bar.detail.map.BarDetailFullScreenMapFragment;
import app.delivering.component.bar.map.cluster.TitledMarkerCreator;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class BarDetailMapFragmentBinder extends BaseBinder {
    private final String color;
    private final String title;
    @BindView(R.id.map_view) MapView mapView;
    private BarDetailFullScreenMapFragment fragment;
    private final InitExceptionHandler initExceptionHandler;
    private TitledMarkerCreator markerCreator;

    public BarDetailMapFragmentBinder(BarDetailFullScreenMapFragment fragment) {
        super(fragment.getBaseActivity());
        this.fragment = fragment;
        initExceptionHandler = new InitExceptionHandler(getActivity());
        if (fragment.getArguments() != null) {
            String address = fragment.getArguments().getString(BarDetailFullScreenMapFragment.MARKER_ADDRESS);
            String city = fragment.getArguments().getString(BarDetailFullScreenMapFragment.MARKER_CITY);
            String state = fragment.getArguments().getString(BarDetailFullScreenMapFragment.MARKER_STATE);
            String zip = fragment.getArguments().getString(BarDetailFullScreenMapFragment.MARKER_ZIP);
            color = fragment.getArguments().getString(BarDetailFullScreenMapFragment.MARKER_COLOR);
            title = String.format(getString(R.string.vendor_address_two_rows_form), address, city, state, zip);
        } else {
            title = getString(R.string.venue_closed);
            color = String.valueOf(getActivity().getResources().getColor(R.color.color_5b606f));
        }
        markerCreator = new TitledMarkerCreator(getActivity(), R.layout.map_marker_icon, R.id.map_marker_title);
    }

    @Override
    public void afterViewsBounded() {
        MapObservable.get(mapView)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::showDetailOnMap, this::onError);
    }

    private void showDetailOnMap(GoogleMap map) {
        map.setPadding(0, getActivity().getResources().getDimensionPixelSize(R.dimen.dip60), 0, 0);
        LatLng point = new LatLng(0, 0);
        if (fragment.getArguments() != null) {
            point = fragment.getArguments().getParcelable(BarDetailFullScreenMapFragment.TARGET_POSITION);
        }
        markerCreator.addMarker(map, point, color, title);
        map.setOnMarkerClickListener(this::onMarkerClick);
        addUserMarker(map);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 12));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(true);
    }

    private boolean onMarkerClick(Marker marker) {
        marker.setIcon(markerCreator.getMarkerBitmap(title, Color.parseColor(color)));
        return true;
    }

    private void addUserMarker(GoogleMap map) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                map.setMyLocationEnabled(true);
        } else
            map.setMyLocationEnabled(true);
    }

    private void onError(Throwable throwable) {
        initExceptionHandler.showError(throwable, view -> afterViewsBounded());
    }
}