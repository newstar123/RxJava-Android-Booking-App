package app.delivering.mvp.ride.order.address.apply.standard.binder;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.location.places.Place;

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
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import app.delivering.mvp.ride.order.init.frombar.events.FromBarAddressEvent;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.route.apply.custom.events.ApplyPickUpAddressEvent;
import app.delivering.mvp.ride.order.type.update.events.UpdateTypeWithFareEvent;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


public class ApplyAddressBinder extends BaseBinder {
    @BindView(R.id.order_ride_information_section) View orderRideInformationSection;
    @BindViews({R.id.order_ride_address_divider_horizontal,
            R.id.order_ride_address,
            R.id.order_ride_bar}) List<View> orderViews;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;
    @BindView(R.id.order_ride_bar) View vendorAddress;
    @BindView(R.id.order_ride_address_focus_thief) LinearLayout orderRideAddressFocusThief;
    @BindView(R.id.order_ride_advert) ImageView orderRideAdvertImageView;
    @BindView(R.id.order_ride_advert_container) LinearLayout orderRideAdvertContainer;
    @BindView(R.id.order_ride_type_progress) MaterialProgressBar rideTypeProgressBar;

    public ApplyAddressBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderRideTypeWasChanged(ApplyAddressEvent event) {
        apply(event.getModel(), false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderPickUpFieldWasChanged(ApplyPickUpAddressEvent event) {
        apply(event.getModel(), true);
    }

    private void apply(Place place, boolean isPickUpWasChanged) {
      //Deleting info about the prices from prev ride(route) here
        EventBus.getDefault().removeStickyEvent(UpdateTypeWithFareEvent.class);
        OrderRideAnimation.run(getActivity());
        restoreOrderViewsAfterAddressPrediction();
        rideTypeProgressBar.setVisibility(View.VISIBLE);
        modifyInformationSection();
        applyChangedAddress(place, isPickUpWasChanged);
        hideSoftKeyboard();
        clearFocus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFromBarAddressEvent(FromBarAddressEvent event) {
        OrderRideAnimation.run(getActivity());
        restoreOrderViewsAfterAddressPrediction();
        modifyInformationSection();
        hideSoftKeyboard();
        clearFocus();
    }

    private void restoreOrderViewsAfterAddressPrediction() {
        ButterKnife.apply(orderViews, ViewActionSetter.VISIBLE);
        if (getRideType() == InitialRideType.CUSTOM) {
            vendorAddress.setVisibility(View.GONE);
            orderPickUpAddress.setVisibility(View.VISIBLE);
        }
        if (null != orderRideAdvertImageView.getDrawable())
            orderRideAdvertContainer.setVisibility(View.VISIBLE);
    }

    private InitialRideType getRideType() {
        int value = getActivity().getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }

    private void modifyInformationSection() {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) orderRideInformationSection.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.height = (int) getActivity().getResources().getDimension(R.dimen.dip270);
        orderRideInformationSection.setLayoutParams(layoutParams);
    }

    private void applyChangedAddress(Place place, boolean isPickUpWasChanged) {
        if (isPickUpWasChanged){
            orderPickUpAddress.setText(place.getAddress());
            orderPickUpAddress.setTag(place);
        } else {
            orderRideAddressEditText.setText(place.getAddress());
            orderRideAddressEditText.setTag(place);
        }
    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void clearFocus() {
        orderRideAddressEditText.clearFocus();
        orderRideAddressFocusThief.requestFocus();
    }


}
