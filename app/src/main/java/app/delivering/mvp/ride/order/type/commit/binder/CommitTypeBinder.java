package app.delivering.mvp.ride.order.type.commit.binder;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.advert.VideoAdvertActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import app.delivering.mvp.ride.order.fare.apply.model.OnOrderRideFareReset;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.init.frombar.binder.FromBarInitBinder;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.map.moved.model.OnMapCameraMoved;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import app.delivering.mvp.ride.order.route.apply.vendor.events.ClearMapEvent;
import app.delivering.mvp.ride.order.type.commit.binder.subbinder.GetPoolCapacitySubBinder;
import app.delivering.mvp.ride.order.type.commit.binder.subbinder.GetRideDirectionSubBinder;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;
import app.delivering.mvp.ride.order.type.pool.capacity.show.events.OnShowPoolCapacityEvent;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CommitTypeBinder extends BaseBinder {
    private final GetPoolCapacitySubBinder getPoolCapacitySubBinder;
    private final GetRideDirectionSubBinder getRideDirectionSubBinder;
    @BindViews({R.id.order_ride_type_progress,
            R.id.order_ride_address_divider_horizontal,
            R.id.order_ride_address,
            R.id.order_pick_up_address,
            R.id.order_ride_pickup_estimate,
            R.id.order_ride_type_pager_indicator,
            R.id.order_ride_type_pager,
            R.id.order_ride_commit_ride_type,
            R.id.root_order_ride_bar}) List<View> orderViews;
    @BindView(R.id.order_ride_commit_ride_type) TextView commitRideTypeTextView;
    @BindViews({R.id.order_ride_confirm_address_title,
            R.id.order_ride_confirm_address,
            R.id.order_ride_departure_pointer,
            R.id.order_ride_start_ride}) List<View> commitViews;
    @BindViews({R.id.order_ride_pool_capacity,
            R.id.order_ride_pool_question,
            R.id.order_ride_pool_divider,
            R.id.order_ride_pool_fare,
            R.id.order_ride_pool_title}) List<View> poolCapacityViews;
    @BindView(R.id.order_ride_confirm_address) TextView orderRideConfirmAddressTextView;
    @BindView(R.id.order_ride_information_section_align_for_map) View informationSectionAlignForMap;
    @BindView(R.id.order_ride_information_section) View informationSection;
    @BindView(R.id.order_ride_map) MapView mapView;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;
    @BindView(R.id.order_ride_start_ride) TextView startRideTextView;
    @BindView(R.id.order_ride_bar) TextView orderRideBarTextView;

    public CommitTypeBinder(BaseActivity activity) {
        super(activity);
        getPoolCapacitySubBinder = new GetPoolCapacitySubBinder(activity);
        getRideDirectionSubBinder = new GetRideDirectionSubBinder(activity);
    }

    @OnClick(R.id.order_ride_commit_ride_type) void commitRideType() {
        RideType rideType = (RideType) commitRideTypeTextView.getTag();
        String commitButtonTitle = commitRideTypeTextView.getText().toString();
        if (rideType == null || TextUtils.isEmpty(rideType.getName()))
            return;
        else if (rideType.getName().equals(RideCategoryFactory.POOL) &&
                (!TextUtils.isEmpty(commitButtonTitle) ? "" : commitButtonTitle).equals(getString(R.string.confirm_seats)))
            EventBus.getDefault().post(new OnShowPoolCapacityEvent());
        else if (FromBarInitBinder.getRideType(getActivity()) == InitialRideType.FROM_THE_VENUE)
            onShowAdvert();
        else
            goDeparturePoint();
    }

    private void onShowAdvert() {
        Intent intent = new Intent(getActivity(), VideoAdvertActivity.class);
        List<LatLng> route = (List<LatLng>) mapView.getTag();
        intent.putExtra(VideoAdvertActivity.DEPARTURE_KEY, route.get(0));
        intent.putExtra(VideoAdvertActivity.DESTINATION_KEY, route.get(route.size() - 1));
        RideType rideType = (RideType) commitRideTypeTextView.getTag();
        intent.putExtra(VideoAdvertActivity.FARE_ID_KEY, rideType.getFareId());
        intent.putExtra(VideoAdvertActivity.FARE_EXPIRED_AT_KEY, rideType.getFareExpiredAt());
        intent.putExtra(VideoAdvertActivity.PRODUCT_ID_KEY, rideType.getProductId());
        if (getRideType() == InitialRideType.CUSTOM)
            intent.putExtra(VideoAdvertActivity.DEPARTURE_ADDRESS_KEY, orderPickUpAddress.getText().toString());
        else
            intent.putExtra(VideoAdvertActivity.DEPARTURE_ADDRESS_KEY, orderRideBarTextView.getText().toString());
        Place place = (Place) orderRideAddressEditText.getTag();
        intent.putExtra(VideoAdvertActivity.DESTINATION_ADDRESS_KEY, Objects.requireNonNull(place.getAddress()).toString());
        double discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        intent.putExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, discount);
        if (rideType.getName().equals(RideCategoryFactory.POOL))
            intent.putExtra(VideoAdvertActivity.CAPACITY_KEY, getPoolCapacitySubBinder.getPoolCapacity());
        intent.putExtra(VideoAdvertActivity.RIDE_DIRECTION_KEY, getRideDirectionSubBinder.getRideDirection());
        OnOrderRideFareReset.send();
        getActivity().startActivityForResult(intent, OrderRideActivity.ORDER_RIDE_ADVERT_REQUEST);
    }

    private void goDeparturePoint() {
        OrderRideAnimation.run(getActivity());
        ButterKnife.apply(orderViews, ViewActionSetter.GONE);
        ButterKnife.apply(poolCapacityViews, ViewActionSetter.GONE);
        ButterKnife.apply(commitViews, ViewActionSetter.VISIBLE);
        startRideTextView.setClickable(false);
        resizeInformationSectionAlignForMap();
        resizeInformationSection();
        clearMapMarkers();
        setUpMapToCommitMode();
        setDepartureText();
    }

    private void resizeInformationSectionAlignForMap() {
        ViewGroup.LayoutParams layoutParams = informationSectionAlignForMap.getLayoutParams();
        layoutParams.height = (int) getActivity().getResources().getDimension(R.dimen.dip164);
        informationSectionAlignForMap.setLayoutParams(layoutParams);
    }

    private void resizeInformationSection() {
        ViewGroup.LayoutParams layoutParams = informationSection.getLayoutParams();
        layoutParams.height = (int) getActivity().getResources().getDimension(R.dimen.dip164);
        informationSection.setLayoutParams(layoutParams);
    }

    private void clearMapMarkers() {
        EventBus.getDefault().post(new ClearMapEvent());
    }

    private void setUpMapToCommitMode() {
        MapObservable.get(mapView).subscribe(this::setUpMapToCommitMode, err -> {});
    }

    private void setUpMapToCommitMode(GoogleMap googleMap) {
        showUserBlueDote(googleMap);
    }

    private void showUserBlueDote(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        Place place = null;
        if (getRideType() == InitialRideType.CUSTOM)
            place = (Place) orderPickUpAddress.getTag();
        else
            place = (Place) orderRideAddressEditText.getTag();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15), new GoogleMap.CancelableCallback() {
            @Override public void onFinish() {
                setUpMapOnCameraChangeHandling(googleMap);
            }

            @Override public void onCancel() {

            }
        });
    }

    private void setUpMapOnCameraChangeHandling(GoogleMap googleMap) {
        googleMap.setOnCameraIdleListener(() -> sendMapCameraMovedEvent(googleMap));
    }

    private void sendMapCameraMovedEvent(GoogleMap googleMap) {
        LatLng target = googleMap.getCameraPosition().target;
        EventBus.getDefault().post(new OnMapCameraMoved(target));
        setDepartureText();
    }

    private void setDepartureText() {
        Place place = null;
        if (getRideType() == InitialRideType.CUSTOM)
            place = (Place) orderPickUpAddress.getTag();
        else
            place = (Place) orderRideAddressEditText.getTag();
        orderRideConfirmAddressTextView.setText(place.getAddress().toString());
    }

    private InitialRideType getRideType() {
        int value = getActivity().getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }
}
