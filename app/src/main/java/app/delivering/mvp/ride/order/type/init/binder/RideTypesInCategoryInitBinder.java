package app.delivering.mvp.ride.order.type.init.binder;


import android.content.res.Resources;
import android.graphics.drawable.StateListDrawable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.component.ride.order.type.OrderRideTypeFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.detail.show.model.OnRideTypeClicked;
import app.delivering.mvp.ride.order.type.init.binder.event.OrderRideTypeChangedEvent;
import app.delivering.mvp.ride.order.type.init.binder.legacy.ColorStateListSupportLegacy;
import app.delivering.mvp.ride.order.type.init.binder.legacy.HtmlConverter;
import app.delivering.mvp.ride.order.type.init.presenter.OrderRideTypeInitPresenter;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

public class RideTypesInCategoryInitBinder extends BaseBinder {
    private final OrderRideTypeChangedEvent orderRideTypeChangedEvent;
    @BindView(R.id.ride_type_radio_group) RadioGroup rideTypeRadioGroup;
    private final OrderRideTypeInitPresenter presenter;
    private BaseFragment fragment;

    public RideTypesInCategoryInitBinder(BaseFragment fragment) {
        super(fragment.getBaseActivity());
        this.fragment = fragment;
        presenter = new OrderRideTypeInitPresenter(getActivity());
        orderRideTypeChangedEvent = new OrderRideTypeChangedEvent(fragment);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        String categoryName = fragment.getArguments().getString(OrderRideTypeFragment.ORDER_TYPE_ID);
        presenter.process(categoryName)
                .observeOn(AndroidSchedulers.mainThread())
                .map(RideCategory::getRideTypes)
                .doOnNext(rides -> Collections.sort(rides, this::sortByCapacity))
                .flatMapIterable(items -> items)
                .doOnNext(this::show)
                .toList()
                .subscribe(this::setActive, Throwable::printStackTrace);
    }

    private int sortByCapacity(RideType rideType1, RideType rideType2){
        return Integer.parseInt(rideType1.getCapacity()) - Integer.parseInt(rideType2.getCapacity());
    }

    public void show(RideType rideType) {
        RadioButton rideTypeButton = new RadioButton(getActivity());
        rideTypeButton.setId(View.NO_ID);
        rideTypeButton.setTag(rideType);
        Resources resources = getActivity().getResources();
        rideTypeButton.setCompoundDrawablesWithIntrinsicBounds(0, rideType.getImageId(), 0 ,0);

        if (isRideFree(rideType))
            rideTypeButton.setText(getString(R.string.free));
        else {
            String filledTemplate = createFareString(rideType);
            Spanned spanned = HtmlConverter.formatHtmlToSpanned(filledTemplate);
            rideTypeButton.setText(spanned);
        }

        int width = (int) resources.getDimension(R.dimen.dip120);
        int height = (int) resources.getDimension(R.dimen.dip141);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
        rideTypeButton.setLayoutParams(layoutParams);
        rideTypeButton.setGravity(Gravity.CENTER);
        rideTypeButton.setTextSize(16f);
        rideTypeButton.setPadding(0, (int) resources.getDimension(R.dimen.dip35), 0, 0);
        rideTypeButton.setButtonDrawable(new StateListDrawable());
        rideTypeButton.setTextColor(ColorStateListSupportLegacy.get(getActivity(), R.color.selector_uber_type_text_color));
        rideTypeButton.setOnClickListener(this::onRideTypeClick);
        rideTypeRadioGroup.addView(rideTypeButton);
    }

    private boolean isRideFree(RideType rideType) {
        if (!TextUtils.isEmpty(rideType.getFare())) {
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

    private void setActive(List<RideType> rideTypeList){
        rideTypeRadioGroup.setOnCheckedChangeListener(orderRideTypeChangedEvent::send);
        View childAt = rideTypeRadioGroup.getChildAt(0);
        rideTypeRadioGroup.check(childAt.getId());
    }

    private void onRideTypeClick(View view) {
        RideType rideType = (RideType) view.getTag();
        EventBus.getDefault().post(new OnRideTypeClicked(rideType));
    }

}
