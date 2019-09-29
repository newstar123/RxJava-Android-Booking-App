package app.delivering.mvp.ride.order.type.select.binder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.pager.RideTypeAdapter;
import app.delivering.component.ride.order.type.OrderRideTypeFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.init.model.OrderRideTypeWasChanged;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;
import app.delivering.mvp.ride.order.type.select.binder.subbinder.ArcRouteOnMapSubBinder;
import app.delivering.mvp.ride.order.type.select.binder.subbinder.RouteOnMapSubBinder;
import butterknife.BindView;

public class SelectRideBinder extends BaseBinder {
    @BindView(R.id.order_ride_type_pager) ViewPager orderRideTypeViewPager;
    @BindView(R.id.order_ride_pickup_estimate) TextView orderRidePickupEstimateTextView;
    @BindView(R.id.order_ride_commit_ride_type) TextView orderRideCommitRideTypeTextView;
    @BindView(R.id.order_ride_pool_capacity_1) RadioButton poolCapacity1View;
    @BindView(R.id.order_ride_map) MapView mapView;
    private final ArcRouteOnMapSubBinder arcRouteOnMapSubBinder;
    private final RouteOnMapSubBinder routeOnMapSubBinder;

    public SelectRideBinder(BaseActivity activity) {
        super(activity);
        arcRouteOnMapSubBinder = new ArcRouteOnMapSubBinder(activity);
        routeOnMapSubBinder = new RouteOnMapSubBinder(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderRideTypeWasChanged(OrderRideTypeWasChanged event) {
        String activeCategoryName = getActiveCategoryName();
        if (activeCategoryName.equals(event.getCategoryName()))
            change(event.getRideType());
    }

    private String getActiveCategoryName() {
        int currentItem = orderRideTypeViewPager.getCurrentItem();
        Fragment item = ((RideTypeAdapter) orderRideTypeViewPager.getAdapter()).getItem(currentItem);
        Bundle arguments = item.getArguments();
        return arguments.getString(OrderRideTypeFragment.ORDER_TYPE_ID);
    }

    private void change(RideType rideType) {
        showPickUpEstimation(rideType);
        if (wasRideShareTypeChanged(rideType))
            changePolylineType(rideType);
        if (rideType.getName().equals(RideCategoryFactory.POOL))
            poolCapacity1View.setTag(rideType);
        showSelectedRideType(rideType);
    }

    private void showPickUpEstimation(RideType rideType) {
        String templateEstimate = getString(R.string.pickup_estimate_template);
        String pickupEstimate = rideType.getPickupEstimate();
        String filledTemplateEstimate = String.format(templateEstimate, pickupEstimate);
        OrderRideAnimation.run(getActivity());
        if (hasEstimate(pickupEstimate)) {
            orderRidePickupEstimateTextView.setVisibility(View.GONE);
            orderRidePickupEstimateTextView.setText("");
        } else
            orderRidePickupEstimateTextView.setVisibility(View.VISIBLE);
        orderRidePickupEstimateTextView.setText(filledTemplateEstimate);
    }

    private boolean hasEstimate(String pickupEstimate) {
        return TextUtils.isEmpty(pickupEstimate)
                || pickupEstimate.equals("null")
                || orderRideCommitRideTypeTextView.getVisibility() == View.GONE;
    }

    private boolean wasRideShareTypeChanged(RideType rideType) {
        RideType lastRideType = (RideType) orderRideCommitRideTypeTextView.getTag();
        return (lastRideType == null || TextUtils.isEmpty(lastRideType.getName()) || !lastRideType.getName().equals(rideType.getName()));
    }

    private void changePolylineType(RideType rideType) {
        if (rideType.getName().equals(RideCategoryFactory.POOL))
            arcRouteOnMapSubBinder.apply(mapView, (List<LatLng>) mapView.getTag());
        else
            routeOnMapSubBinder.apply(mapView, (List<LatLng>) mapView.getTag());
    }

    private void showSelectedRideType(RideType rideType) {
        String typeNameUpperCase = String.format(getString(R.string.request_type), rideType.getName())
                .toUpperCase();
        orderRideCommitRideTypeTextView.setText(typeNameUpperCase);
        orderRideCommitRideTypeTextView.setTag(rideType);
    }
}
