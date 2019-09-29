package app.delivering.component.bar.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.bars.map.init.binder.BarListMapInitBinder;
import app.delivering.mvp.bars.map.lifecycle.binder.BarListMapLifecycleBinder;
import app.delivering.mvp.bars.map.lifecycle.events.OnDestroyMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnLowMemoryMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnPauseMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnResumeMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnSaveInstanceStateMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnStartMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnStopMapEvent;
import app.delivering.mvp.bars.map.user.binder.UserOnMapBinder;

public class BarMapFragment extends BaseFragment {
    private MapView mapView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bar_list_map, container, false);
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.onResume();
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        BarListMapLifecycleBinder lifecycleBinder = new BarListMapLifecycleBinder(this);
        addToEventBusAndViewInjection(lifecycleBinder, rootView);
        BarListMapInitBinder initBinder = new BarListMapInitBinder(this);
        addToEventBusAndViewInjection(initBinder, rootView);
        UserOnMapBinder userOnMapBinder = new UserOnMapBinder(getBaseActivity());
        addToEventBusAndViewInjection(userOnMapBinder, rootView);
    }

    @Override public void onStart() {
        super.onStart();
        EventBus.getDefault().post(new OnStartMapEvent());
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new OnResumeMapEvent());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().post(new OnPauseMapEvent());
    }

    @Override public void onStop() {
        super.onStop();
        EventBus.getDefault().post(new OnStopMapEvent());
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        EventBus.getDefault().post(new OnSaveInstanceStateMapEvent(outState));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new OnDestroyMapEvent());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        EventBus.getDefault().post(new OnLowMemoryMapEvent());
    }
}
