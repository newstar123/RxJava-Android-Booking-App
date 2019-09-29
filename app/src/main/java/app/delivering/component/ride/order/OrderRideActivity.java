package app.delivering.component.ride.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.MapView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.ride.order.address.apply.standard.binder.ApplyAddressBinder;
import app.delivering.mvp.ride.order.address.change.binder.ChangeAddressBinder;
import app.delivering.mvp.ride.order.address.edit.binder.EditAddressBinder;
import app.delivering.mvp.ride.order.address.enter.binder.OnAddressEnterBinder;
import app.delivering.mvp.ride.order.address.focus.binder.FocusAddressBinder;
import app.delivering.mvp.ride.order.address.unfocus.binder.UnFocusAddressBinder;
import app.delivering.mvp.ride.order.address.unfocus.events.OrderRideActivityOnPressBackEvent;
import app.delivering.mvp.ride.order.advert.image.binder.ImageAdvertBinder;
import app.delivering.mvp.ride.order.advert.video.binder.VideoAdvertBinder;
import app.delivering.mvp.ride.order.close.binder.CloseOrderRideBinder;
import app.delivering.mvp.ride.order.close.events.CloseRideEvent;
import app.delivering.mvp.ride.order.fare.apply.binder.ApplyFareBinder;
import app.delivering.mvp.ride.order.fare.apply.model.OnOrderRideFareReset;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.init.actionbar.binder.InitActionBarBinder;
import app.delivering.mvp.ride.order.init.custom.DeferredRideInitBinder;
import app.delivering.mvp.ride.order.init.frombar.binder.FromBarInitBinder;
import app.delivering.mvp.ride.order.init.tobar.binder.ToBarInitBinder;
import app.delivering.mvp.ride.order.map.moved.binder.MapCameraMovedBinder;
import app.delivering.mvp.ride.order.route.apply.custom.binder.ApplyCustomRouteBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.ApplyRouteBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.PolylineAnimator;
import app.delivering.mvp.ride.order.type.apply.binder.ApplyTypesBinder;
import app.delivering.mvp.ride.order.type.commit.binder.CommitTypeBinder;
import app.delivering.mvp.ride.order.type.detail.show.RideTypeDetailShowBinder;
import app.delivering.mvp.ride.order.type.pool.capacity.change.binder.ChangePoolCapacityBinder;
import app.delivering.mvp.ride.order.type.pool.capacity.hide.binder.HidePoolCapacityBinder;
import app.delivering.mvp.ride.order.type.pool.capacity.show.binder.ShowPoolCapacityBinder;
import app.delivering.mvp.ride.order.type.select.binder.SelectRideBinder;
import app.delivering.mvp.ride.order.type.uncommit.binder.UnCommitTypeBinder;
import app.gateway.uber.UberLoginManagerInstance;
import rx.Observable;


public class OrderRideActivity extends BaseActivity {
    public final static int ORDER_RIDE_REQUEST = 4350;
    public final static String ORDER_RIDE_FROM_BAR_KEY = "ORDER_RIDE_FROM_BAR_KEY";
    public final static String ORDER_RIDE_DISCOUNT_KEY = "ORDER_RIDE_DISCOUNT_KEY";
    public final static int ORDER_RIDE_ADVERT_REQUEST = 4351;
    private final static String BUNDLE_KEY_MAP_STATE = "BUNDLE_KEY_MAP_STATE_DATA";
    private List<Object> onActivityResultEvents;
    private MapView mapView;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ride);
        onActivityResultEvents = new ArrayList<>();
        mapView = findViewById(R.id.order_ride_map);
        Bundle mapState = null;
        if (savedInstanceState != null)
            mapState = savedInstanceState.getBundle(BUNDLE_KEY_MAP_STATE);
        mapView.onCreate(mapState);
        setUpUseCases();
    }

    private void setUpUseCases() {
        InitActionBarBinder initActionBarBinder = new InitActionBarBinder(this);
        addItemForViewsInjection(initActionBarBinder);
        ImageAdvertBinder imageAdvertBinder = new ImageAdvertBinder(this);
        addToEventBusAndViewInjection(imageAdvertBinder);
        FromBarInitBinder fromBarInitBinder = new FromBarInitBinder(this);
        addToEventBusAndViewInjection(fromBarInitBinder);
        ToBarInitBinder toBarInitBinder = new ToBarInitBinder(this);
        addToEventBusAndViewInjection(toBarInitBinder);
        DeferredRideInitBinder deferredRideInitBinder = new DeferredRideInitBinder(this);
        addToEventBusAndViewInjection(deferredRideInitBinder);
        SelectRideBinder selectRideBinder = new SelectRideBinder(this);
        addToEventBusAndViewInjection(selectRideBinder);
        EditAddressBinder editAddressBinder = new EditAddressBinder(this);
        addToEventBusAndViewInjection(editAddressBinder);
        FocusAddressBinder focusAddressBinder = new FocusAddressBinder(this);
        addItemForViewsInjection(focusAddressBinder);
        ApplyAddressBinder applyAddressBinder = new ApplyAddressBinder(this);
        addToEventBusAndViewInjection(applyAddressBinder);
        ApplyRouteBinder applyRouteBinder = new ApplyRouteBinder(this);
        addToEventBusAndViewInjection(applyRouteBinder);
        ApplyCustomRouteBinder customRouteBinder = new ApplyCustomRouteBinder(this);
        addToEventBusAndViewInjection(customRouteBinder);
        ApplyTypesBinder applyTypesBinder = new ApplyTypesBinder(this);
        addToEventBusAndViewInjection(applyTypesBinder);
        ApplyFareBinder applyFareBinder = new ApplyFareBinder(this);
        addToEventBusAndViewInjection(applyFareBinder);
        CommitTypeBinder commitTypeBinder = new CommitTypeBinder(this);
        addItemForViewsInjection(commitTypeBinder);
        MapCameraMovedBinder mapCameraMovedBinder = new MapCameraMovedBinder(this);
        addToEventBusAndViewInjection(mapCameraMovedBinder);
        UnCommitTypeBinder unCommitTypeBinder = new UnCommitTypeBinder(this);
        addToEventBusAndViewInjection(unCommitTypeBinder);
        UnFocusAddressBinder unFocusAddressBinder = new UnFocusAddressBinder(this);
        addToEventBusAndViewInjection(unFocusAddressBinder);
        VideoAdvertBinder videoAdvertBinder = new VideoAdvertBinder(this);
        addItemForViewsInjection(videoAdvertBinder);
        CloseOrderRideBinder closeOrderRideBinder = new CloseOrderRideBinder(this);
        addToEventBusAndViewInjection(closeOrderRideBinder);
        ChangeAddressBinder changeAddressBinder = new ChangeAddressBinder(this);
        addToEventBusAndViewInjection(changeAddressBinder);
        RideTypeDetailShowBinder rideTypeDetailShowBinder = new RideTypeDetailShowBinder(this);
        addToEventBusAndViewInjection(rideTypeDetailShowBinder);
        ShowPoolCapacityBinder showPoolCapacityBinder = new ShowPoolCapacityBinder(this);
        addToEventBusAndViewInjection(showPoolCapacityBinder);
        HidePoolCapacityBinder hidePoolCapacityBinder = new HidePoolCapacityBinder(this);
        addToEventBusAndViewInjection(hidePoolCapacityBinder);
        ChangePoolCapacityBinder changePoolCapacityBinder = new ChangePoolCapacityBinder(this);
        addToEventBusAndViewInjection(changePoolCapacityBinder);
        OnAddressEnterBinder onAddressEnterBinder = new OnAddressEnterBinder(this);
        addItemForViewsInjection(onAddressEnterBinder);
    }

    @Override protected void onStart() {
        super.onStart();
        mapView.onStart();
        EventBus.getDefault().post(new OnOrderRideStartEvent());
        Observable.from(onActivityResultEvents)
                .doOnNext(event -> EventBus.getDefault().post(event))
                .toList()
                .subscribe(events -> onActivityResultEvents = new ArrayList<>());
    }

    @Override protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        PolylineAnimator.getInstance().stop();
        OnOrderRideFareReset.send();
    }

    @Override protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UberLoginManagerInstance.onActivityResult(this, requestCode, resultCode, data);
        if (ORDER_RIDE_ADVERT_REQUEST == requestCode && resultCode == Activity.RESULT_OK)
            onActivityResult(new CloseRideEvent());
    }

    private void onActivityResult(Object result) {
        if (isStart())
            EventBus.getDefault().post(result);
        else
            onActivityResultEvents.add(result);
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        Bundle mapState = new Bundle();
        if (mapView != null)
            mapView.onSaveInstanceState(mapState);
        outState.putBundle(BUNDLE_KEY_MAP_STATE, mapState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onBackPressed() {
        if (isInChooseTypeMode() && isNotPoolCapacity())
            super.onBackPressed();
        else
            EventBus.getDefault().post(new OrderRideActivityOnPressBackEvent());
    }

    private boolean isNotPoolCapacity() {
        return findViewById(R.id.order_ride_pool_title).getVisibility() != View.VISIBLE;
    }

    private boolean isInChooseTypeMode() {
        int height = findViewById(R.id.order_ride_information_section).getLayoutParams().height;
        int round = Math.round(getResources().getDimension(R.dimen.dip270));
        return height + 1 == round || height - 1 == round || round == height;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
