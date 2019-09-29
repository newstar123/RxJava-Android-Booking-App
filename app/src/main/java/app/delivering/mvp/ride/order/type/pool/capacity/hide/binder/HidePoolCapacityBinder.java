package app.delivering.mvp.ride.order.type.pool.capacity.hide.binder;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.address.unfocus.events.OrderRideActivityOnPressBackEvent;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


public class HidePoolCapacityBinder extends BaseBinder {
    @BindViews({R.id.order_ride_address_divider_horizontal,
            R.id.order_ride_address,
            R.id.order_ride_pickup_estimate,
            R.id.order_ride_type_pager_indicator,
            R.id.order_ride_type_pager,
            R.id.root_order_ride_bar}) List<View> orderViews;
    @BindViews({R.id.order_ride_pool_capacity,
            R.id.order_ride_pool_question,
            R.id.order_ride_pool_divider,
            R.id.order_ride_pool_fare,
            R.id.order_ride_pool_title}) List<View> poolCapacityViews;
    @BindView(R.id.order_ride_pool_capacity_2) RadioButton poolCapacity2View;
    @BindView(R.id.order_ride_pool_fare) View poolFareView;
    @BindView(R.id.order_ride_commit_ride_type) TextView commitRideTypeTextView;

    public HidePoolCapacityBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OrderRideActivityOnPressBackEvent event) {
        if (isConfirmPoolCapacity())
            showOrderViews();
    }

    private boolean isConfirmPoolCapacity() {
        return poolFareView.getVisibility() == View.VISIBLE;
    }

    private void showOrderViews() {
        RideType rideType = (RideType) commitRideTypeTextView.getTag();
        String typeNameUpperCase = String.format(getString(R.string.request_type), rideType.getName())
                .toUpperCase();
        commitRideTypeTextView.setText(typeNameUpperCase);
        ButterKnife.apply(orderViews, ViewActionSetter.VISIBLE);
        ButterKnife.apply(poolCapacityViews, ViewActionSetter.GONE);
        poolCapacity2View.setTag(null);
    }

}
