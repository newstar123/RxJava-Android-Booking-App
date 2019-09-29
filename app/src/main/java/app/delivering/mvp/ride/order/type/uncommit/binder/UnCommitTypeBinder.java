package app.delivering.mvp.ride.order.type.uncommit.binder;


import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.address.apply.standard.events.ApplyAddressEvent;
import app.delivering.mvp.ride.order.address.unfocus.events.OrderRideActivityOnPressBackEvent;
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.route.apply.custom.events.ApplyPickUpAddressEvent;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class UnCommitTypeBinder extends BaseBinder {
    @BindViews({R.id.order_ride_confirm_address_title,
            R.id.order_ride_confirm_address,
            R.id.order_ride_departure_pointer,
            R.id.order_ride_start_ride}) List<View> commitViews;
    @BindView(R.id.order_ride_information_section_align_for_map) View informationSectionAlignForMap;
    @BindView(R.id.order_ride_map) MapView mapView;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;

    public UnCommitTypeBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OrderRideActivityOnPressBackEvent event) {
        if (isInCommitedTypeMode())
            unCommitType();
    }

    private boolean isInCommitedTypeMode() {
        return informationSectionAlignForMap.getLayoutParams().height
                == (int) getActivity().getResources().getDimension(R.dimen.dip164);
    }

    private void unCommitType() {
        OrderRideAnimation.run(getActivity());
        ButterKnife.apply(commitViews, ViewActionSetter.GONE);
        resizeInformationSectionAlignForMap();
        setUpMapToChooseTypeMode();
        restorePreviousCondition();
    }

    private void resizeInformationSectionAlignForMap() {
        ViewGroup.LayoutParams layoutParams = informationSectionAlignForMap.getLayoutParams();
        layoutParams.height = (int) getActivity().getResources().getDimension(R.dimen.dip224);
        informationSectionAlignForMap.setLayoutParams(layoutParams);
    }

    private void setUpMapToChooseTypeMode() {
        MapObservable.get(mapView).subscribe(this::setUpMapToChooseTypeMode, err -> {});
    }

    private void setUpMapToChooseTypeMode(GoogleMap map) {
        map.setMyLocationEnabled(false);
        map.setOnCameraIdleListener(null);
    }

    private void restorePreviousCondition() {
        if (getRideType() == InitialRideType.CUSTOM){
            Place departure = (Place) orderPickUpAddress.getTag();
            EventBus.getDefault().post(new ApplyPickUpAddressEvent(departure));
        }
        Place place = (Place) orderRideAddressEditText.getTag();
        EventBus.getDefault().post(new ApplyAddressEvent(place));
    }

    private InitialRideType getRideType() {
        int value = getActivity().getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }
}
