package app.delivering.mvp.ride.order.address.unfocus.binder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.places.Place;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.adapter.AddressPredictionAdapter;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.address.apply.standard.events.ApplyAddressEvent;
import app.delivering.mvp.ride.order.address.unfocus.events.OrderRideActivityOnPressBackEvent;
import app.delivering.mvp.ride.order.init.frombar.events.FromBarAddressEvent;
import app.delivering.mvp.ride.order.route.apply.custom.events.ApplyPickUpAddressEvent;
import butterknife.BindView;

public class UnFocusAddressBinder extends BaseBinder {
    @BindView(R.id.order_ride_information_section) View orderRideInformationSection;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;
    @BindView(R.id.order_ride_address_prediction) RecyclerView addressPredictionRecyclerView;

    public UnFocusAddressBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OrderRideActivityOnPressBackEvent event) {
        if (isInFocusAddressMode()) {
            restorePreviousCondition();
            restorePickUpAddressField();
        }
    }

    private boolean isInFocusAddressMode() {
        return (int) getActivity().getResources().getDimension(R.dimen.dip100)
                == orderRideInformationSection.getLayoutParams().height;
    }

    private void restorePreviousCondition() {
        Place place = (Place) orderRideAddressEditText.getTag();
        AddressPredictionAdapter adapter = (AddressPredictionAdapter) addressPredictionRecyclerView.getAdapter();
        if (adapter != null)
            adapter.setModels(Collections.emptyList());
        if (place == null) {
            orderRideAddressEditText.setText("");
            EventBus.getDefault().post(new FromBarAddressEvent());
        } else
            EventBus.getDefault().post(new ApplyAddressEvent(place));
    }

    private void restorePickUpAddressField() {
        Place place = (Place) orderPickUpAddress.getTag();
        AddressPredictionAdapter adapter = (AddressPredictionAdapter) addressPredictionRecyclerView.getAdapter();
        if (adapter != null)
            adapter.setModels(Collections.emptyList());
        if (place == null) {
            orderPickUpAddress.setText("");
            EventBus.getDefault().post(new FromBarAddressEvent());
        } else
            EventBus.getDefault().post(new ApplyPickUpAddressEvent(place));
    }
}
