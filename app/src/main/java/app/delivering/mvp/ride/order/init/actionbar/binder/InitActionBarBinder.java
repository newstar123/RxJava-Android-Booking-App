package app.delivering.mvp.ride.order.init.actionbar.binder;

import com.google.android.gms.maps.MapView;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import butterknife.OnClick;

public class InitActionBarBinder extends BaseBinder {
    @BindView(R.id.order_ride_map) MapView mapView;

    public InitActionBarBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        MapObservable.get(mapView).subscribe(map -> map.getUiSettings().setCompassEnabled(false), onErr -> {});
    }

    @OnClick(R.id.uber_back_button) void back(){
        getActivity().onBackPressed();
    }

}
