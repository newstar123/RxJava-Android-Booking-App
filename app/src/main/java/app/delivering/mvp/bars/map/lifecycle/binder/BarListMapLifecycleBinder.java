package app.delivering.mvp.bars.map.lifecycle.binder;

import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.MapView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.bar.map.BarMapFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.bars.map.lifecycle.events.OnCreateMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnDestroyMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnLowMemoryMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnMapRevertAnimationEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnPauseMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnResumeMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnSaveInstanceStateMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnStartMapEvent;
import app.delivering.mvp.bars.map.lifecycle.events.OnStopMapEvent;
import butterknife.BindView;

public class BarListMapLifecycleBinder extends BaseBinder {
    public static final int MAP_ANIMATION_DURATION = 1000;
    @BindView(R.id.map_view) MapView mapView;
    @BindView(R.id.bar_list_map_container) RelativeLayout container;
    private BarMapFragment fragment;

    public BarListMapLifecycleBinder(BarMapFragment fragment) {
        super(fragment.getBaseActivity());
        this.fragment = fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void create(OnCreateMapEvent event) {
        if (mapView != null)
            mapView.onCreate(event.getBundle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void start(OnStartMapEvent event) {
        if (mapView != null)
            mapView.onStart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resume(OnResumeMapEvent event) {
        if (mapView != null) {
            mapView.setVisibility(View.VISIBLE);
            container.setVisibility(View.VISIBLE);
            container.setBackgroundColor(getActivity().getResources().getColor(android.R.color.white));
            mapView.onResume();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void revertAnomation(OnMapRevertAnimationEvent event) {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pause(OnPauseMapEvent event) {
        if (mapView != null)
            mapView.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stop(OnStopMapEvent event) {
        if (mapView != null)
            mapView.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveInstanceState(OnSaveInstanceStateMapEvent event) {
        if (mapView != null)
            mapView.onSaveInstanceState(event.getBundle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void destroy(OnDestroyMapEvent event) {
        if (mapView != null)
            mapView.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void lowMemory(OnLowMemoryMapEvent event) {
        if (mapView != null)
            mapView.onLowMemory();
    }

}
