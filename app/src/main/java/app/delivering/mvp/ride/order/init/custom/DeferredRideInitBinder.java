package app.delivering.mvp.ride.order.init.custom;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.core.location.current.entity.CurrentPlaceResponse;
import app.core.location.current.interactor.CurrentPlaceInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.init.tobar.binder.subbinder.ViewPagerTypeChangeSubBinder;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class DeferredRideInitBinder extends BaseBinder {
    private final CurrentPlaceInteractor getCurrentPlaceInteractor;
    private ReplaySubject<CurrentPlaceResponse> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    private final ViewPagerTypeChangeSubBinder viewPagerTypeChangeSubBinder;
    @BindView(R.id.order_ride_map) MapView mapView;
    @BindView(R.id.root_order_ride_bar) View orderRideBar;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;
    @BindViews({R.id.order_ride_address_divider_horizontal,
            R.id.order_ride_address}) List<View> orderViews;
    @BindView(R.id.order_ride_type_progress) MaterialProgressBar rideTypeProgressBar;

    public DeferredRideInitBinder(BaseActivity activity) {
        super(activity);
        replaySubject = ReplaySubject.create();
        initExceptionHandler = new InitExceptionHandler(getActivity());
        viewPagerTypeChangeSubBinder = new ViewPagerTypeChangeSubBinder(getActivity());
        getCurrentPlaceInteractor = new CurrentPlaceInteractor(activity);
    }

    @Override public void afterViewsBounded() {
        if (getRideType(getActivity()) == InitialRideType.CUSTOM) {
            progressState();
            orderRideBar.setVisibility(View.GONE);
            showRideFromCurrentPosition();
            getCurrentPlaceInteractor.process().subscribe(replaySubject);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OnOrderRideStartEvent event) {
        if (hasToRestore())
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(CurrentPlaceResponse response) {
        resetState();
        viewPagerTypeChangeSubBinder.apply();
        orderPickUpAddress.setVisibility(View.VISIBLE);
        String name = response.getPlaces().get(0).getAddress().toString();
        orderPickUpAddress.setText(name);
        LatLng userLatLng = response.getPlaces().get(0).getLatLng();
        orderPickUpAddress.setTag(response.getPlaces().get(0));
        ButterKnife.apply(orderViews, ViewActionSetter.VISIBLE);
        rideTypeProgressBar.setVisibility(View.GONE);
        orderRideAddressEditText.setVisibility(View.VISIBLE);
        showUserMarker(userLatLng);
    }

    private void showUserMarker(LatLng userLatLng) {
        MapObservable.get(mapView).subscribe(map -> showMarkerAndZoom(userLatLng, map), err -> {});
    }

    private void showMarkerAndZoom(LatLng userLatLng, GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(true);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(userLatLng, 14);
        map.animateCamera(cu, 1000, null);
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> afterViewsBounded());
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartActivity(new OnOrderRideStartEvent());
    }

    private void showRideFromCurrentPosition() {
        moveAddress();
        moveCurrentAddress();
    }

    private void moveAddress() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orderRideAddressEditText.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, R.id.separator_view);
        layoutParams.setMargins(12, 0, 0, 0);
        orderRideAddressEditText.setLayoutParams(layoutParams);
    }

    private void moveCurrentAddress() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) orderPickUpAddress.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ABOVE, 0);
        layoutParams.setMargins(12, 8, 0, 0);
        orderPickUpAddress.setLayoutParams(layoutParams);
    }

    public static InitialRideType getRideType(BaseActivity activity) {
        int value = activity.getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }
}
