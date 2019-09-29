package app.delivering.mvp.ride.order.address.focus.binder;

import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.profile.edit.actionbar.clicks.binder.ViewActionSetter;
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnFocusChange;


public class FocusAddressBinder extends BaseBinder {
    @BindView(R.id.order_ride_information_section) View orderRideInformationSection;
    @BindViews({
            R.id.order_ride_type_pager_indicator,
            R.id.order_ride_type_pager,
            R.id.order_ride_type_progress,
            R.id.order_ride_advert_container,
            R.id.order_ride_pickup_estimate}) List<View> orderViews;

    public FocusAddressBinder(BaseActivity activity) {
        super(activity);
    }

    @OnFocusChange(R.id.order_ride_address) void onFocusAddress(View v, boolean hasFocus) {
        if (hasFocus)
            modify();
    }

    @OnFocusChange(R.id.order_pick_up_address) void onFocusPickUpAddress(View v, boolean hasFocus) {
        if (hasFocus)
            modify();
    }

    private void modify() {
        OrderRideAnimation.run(getActivity());
        ButterKnife.apply(orderViews, ViewActionSetter.GONE);
        modifyInformationSection();
    }

    private void modifyInformationSection() {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) orderRideInformationSection.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.height = (int) getActivity().getResources().getDimension(R.dimen.dip100);
        orderRideInformationSection.setLayoutParams(layoutParams);
    }
}
