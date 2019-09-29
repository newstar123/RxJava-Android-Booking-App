package app.delivering.mvp.ride.order.advert.video.binder;


import android.content.Intent;
import android.location.Address;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.advert.VideoAdvertActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.fare.apply.model.OnOrderRideFareReset;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.commit.binder.subbinder.GetPoolCapacitySubBinder;
import app.delivering.mvp.ride.order.type.commit.binder.subbinder.GetRideDirectionSubBinder;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;
import butterknife.BindView;
import butterknife.OnClick;

public class VideoAdvertBinder extends BaseBinder {
    private final GetPoolCapacitySubBinder getPoolCapacitySubBinder;
    private final GetRideDirectionSubBinder getRideDirectionSubBinder;
    @BindView(R.id.order_ride_map) MapView mapView;
    @BindView(R.id.order_ride_commit_ride_type) TextView orderRideCommitRideTypeTextView;
    @BindView(R.id.order_ride_confirm_address) TextView orderRideConfirmAddressTextView;
    @BindView(R.id.order_ride_bar) TextView orderRideBarTextView;

    public VideoAdvertBinder(BaseActivity activity) {
        super(activity);
        getPoolCapacitySubBinder = new GetPoolCapacitySubBinder(activity);
        getRideDirectionSubBinder = new GetRideDirectionSubBinder(activity);
    }

    @OnClick(R.id.order_ride_start_ride) void onStartOrderButton() {
        Intent intent = new Intent(getActivity(), VideoAdvertActivity.class);
        List<LatLng> route = (List<LatLng>) mapView.getTag();
        Address departure = (Address) orderRideConfirmAddressTextView.getTag();
        intent.putExtra(VideoAdvertActivity.DEPARTURE_KEY, new LatLng(departure.getLatitude(), departure.getLongitude()));
        intent.putExtra(VideoAdvertActivity.DESTINATION_KEY, route.get(route.size() - 1));
        RideType rideType = (RideType) orderRideCommitRideTypeTextView.getTag();
        intent.putExtra(VideoAdvertActivity.FARE_ID_KEY, rideType.getFareId());
        intent.putExtra(VideoAdvertActivity.FARE_EXPIRED_AT_KEY, rideType.getFareExpiredAt());
        intent.putExtra(VideoAdvertActivity.PRODUCT_ID_KEY, rideType.getProductId());
        intent.putExtra(VideoAdvertActivity.DEPARTURE_ADDRESS_KEY, orderRideConfirmAddressTextView.getText().toString());
        intent.putExtra(VideoAdvertActivity.DESTINATION_ADDRESS_KEY, orderRideBarTextView.getText().toString());
        double discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        intent.putExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, discount);
        if (rideType.getName().equals(RideCategoryFactory.POOL))
            intent.putExtra(VideoAdvertActivity.CAPACITY_KEY, getPoolCapacitySubBinder.getPoolCapacity());
        intent.putExtra(VideoAdvertActivity.RIDE_DIRECTION_KEY, getRideDirectionSubBinder.getRideDirection());
        OnOrderRideFareReset.send();
        getActivity().startActivityForResult(intent, OrderRideActivity.ORDER_RIDE_ADVERT_REQUEST);
    }


}
