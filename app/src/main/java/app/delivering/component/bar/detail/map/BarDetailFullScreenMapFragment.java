package app.delivering.component.bar.detail.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.bars.detail.fullscreen.map.actionbar.binder.BarDetailFullScreenMapActionBarBinder;
import app.delivering.mvp.bars.detail.fullscreen.map.init.binder.BarDetailMapFragmentBinder;

public class BarDetailFullScreenMapFragment extends BaseFragment {
    public static final String TARGET_POSITION = "bar_on_map_position";
    public static final String MARKER_ADDRESS = "bar_marker_address";
    public static final String MARKER_CITY = "bar_marker_city";
    public static final String MARKER_STATE = "bar_marker_state";
    public static final String MARKER_ZIP = "bar_marker_zip";
    public static final String MARKER_COLOR = "bar_on_marker_color";
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bar_detail_map, container, false);
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        BarDetailMapFragmentBinder fragmentBinder = new BarDetailMapFragmentBinder(this);
        addItemForViewsInjection(fragmentBinder, rootView);
        BarDetailFullScreenMapActionBarBinder actionBarBinder = new BarDetailFullScreenMapActionBarBinder(this);
        addItemForViewsInjection(actionBarBinder, rootView);
    }

    @Override public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
}
