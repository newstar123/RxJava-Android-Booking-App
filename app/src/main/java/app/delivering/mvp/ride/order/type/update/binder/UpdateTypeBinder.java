package app.delivering.mvp.ride.order.type.update.binder;


import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.delivering.component.ride.order.type.OrderRideTypeFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.init.binder.event.OrderRideTypeChangedEvent;
import app.delivering.mvp.ride.order.type.init.binder.legacy.HtmlConverter;
import app.delivering.mvp.ride.order.type.update.events.UpdateTypeWithFareEvent;
import butterknife.BindView;
import rx.Observable;

public class UpdateTypeBinder extends BaseBinder {
    private final OrderRideTypeChangedEvent orderRideTypeChangedEvent;
    @BindView(R.id.ride_type_radio_group) RadioGroup rideTypeRadioGroup;
    private OrderRideTypeFragment fragment;

    public UpdateTypeBinder(OrderRideTypeFragment fragment) {
        super(fragment.getBaseActivity());
        this.fragment = fragment;
        orderRideTypeChangedEvent = new OrderRideTypeChangedEvent(fragment);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onApplyFare(UpdateTypeWithFareEvent event) {
        String categoryName = fragment.getArguments().getString(OrderRideTypeFragment.ORDER_TYPE_ID);
        RideCategory currentRideCategory = Observable.from(event.getRideCategories())
                .filter(category -> category.getName().equals(categoryName))
                .toBlocking()
                .first();
        Observable.range(0, rideTypeRadioGroup.getChildCount())
                .map(iterator -> rideTypeRadioGroup.getChildAt(iterator))
                .filter(view -> view instanceof RadioButton)
                .subscribe(view -> update(view, currentRideCategory.getRideTypes()));
    }

    private void update(View view, List<RideType> rideTypes) {
        RideType rideTypeOnlyProduct = (RideType) view.getTag();
        RideType currentRideType = Observable.from(rideTypes)
                .filter(rideType -> rideType.getProductId().equals(rideTypeOnlyProduct.getProductId()))
                .toBlocking()
                .first();
        view.setTag(currentRideType);

        if (isRideFree(currentRideType))
            ((RadioButton) view).setText(getString(R.string.free));
        else {
            String filledTemplate = createFareString(currentRideType);
            Spanned spanned = HtmlConverter.formatHtmlToSpanned(filledTemplate);
            ((RadioButton) view).setText(spanned);
        }
        orderRideTypeChangedEvent.send(rideTypeRadioGroup, rideTypeRadioGroup.getCheckedRadioButtonId());
    }

    private boolean isRideFree(RideType rideType) {
        if (!TextUtils.isEmpty(rideType.getFare()) && !TextUtils.isEmpty(rideType.getFarePlusDiscountValue())) {
            String fareStr = rideType.getFare();
            double fare = Double.parseDouble(fareStr.replaceAll(getString(R.string.parse_double_from_string),""));
            return fare <= 0;
        } else
            return false;
    }

    private String createFareString(RideType currentRideType) {
        String template = getString(R.string.ride_type_description_template_discount);
        return String.format(template, currentRideType.getName(), currentRideType.getFare(), currentRideType.getFarePlusDiscountValue());
    }
}
