package app.delivering.mvp.ride.order.type.pool.capacity.show.binder;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.pool.capacity.change.events.OnChangePoolCapacityEvent;
import app.delivering.mvp.ride.order.type.pool.capacity.show.events.OnShowPoolCapacityEvent;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;


public class ShowPoolCapacityBinder extends BaseBinder {
    @BindViews({R.id.order_ride_type_progress,
            R.id.order_ride_address_divider_horizontal,
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
    @BindView(R.id.order_ride_pool_fare) TextView poolFareTextView;
    @BindView(R.id.order_ride_pool_capacity) RadioGroup poolCapacityRadioGroup;
    @BindView(R.id.order_ride_commit_ride_type) TextView commitRideTypeTextView;
    @BindView(R.id.order_ride_pool_capacity_2) RadioButton poolCapacity2View;

    public ShowPoolCapacityBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetupPoolType(OnShowPoolCapacityEvent event) {
        commitRideTypeTextView.setText(R.string.confirm_seats);
        ButterKnife.apply(orderViews, ViewActionSetter.GONE);
        ButterKnife.apply(poolCapacityViews, ViewActionSetter.VISIBLE);
        RideType rideType = (RideType) commitRideTypeTextView.getTag();
        poolCapacityRadioGroup.setOnCheckedChangeListener(this::onChangePoolCapacity);
        if (poolCapacityRadioGroup.getCheckedRadioButtonId() == R.id.order_ride_pool_capacity_1)
            poolFareTextView.setText(rideType.getFare());
        else if (poolCapacity2View.getTag() != null)
            poolFareTextView.setText(((RideType) poolCapacity2View.getTag()).getFare());
    }

    private void onChangePoolCapacity(RadioGroup group, int checkedId) {
        EventBus.getDefault().post(new OnChangePoolCapacityEvent(checkedId));
    }


}
