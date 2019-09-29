package app.delivering.mvp.ride.order.type.detail.show;


import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.pager.RideTypeAdapter;
import app.delivering.component.ride.order.type.OrderRideTypeFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.ride.order.address.apply.standard.events.ApplyAddressEvent;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.detail.show.model.OnRideTypeClicked;
import app.delivering.mvp.ride.order.type.detail.show.subbinder.RideTypeActiveImageFactory;
import app.delivering.mvp.ride.order.type.init.model.OrderRideTypeWasChanged;
import butterknife.BindView;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

public class RideTypeDetailShowBinder extends BaseBinder {
    @BindView(R.id.order_ride_type_detail_name) TextView orderRideTypeDetailName;
    @BindView(R.id.order_ride_type_detail_slogan) TextView orderRideTypeDetailSlogan;
    @BindView(R.id.order_ride_type_detail_arrival) TextView orderRideTypeDetailArrival;
    @BindView(R.id.order_ride_type_detail_arrival_title) TextView orderRideTypeDetailArrivalTitle;
    @BindView(R.id.order_ride_type_detail_capacity) TextView orderRideTypeDetailCapacity;
    @BindView(R.id.order_ride_type_detail_fare) TextView orderRideTypeDetailFare;
    @BindView(R.id.order_ride_type_detail_image) View orderRideTypeDetailImage;
    @BindView(R.id.order_ride_type_details) View orderRideTypeDetails;
    @BindView(R.id.order_ride_type_pager) ViewPager orderRideTypeViewPager;
    private PublishSubject<RideType> rideTypeClicks;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private RideTypeActiveImageFactory rideTypeActiveImageFactory;
    private AtomicInteger clickCounter;

    public RideTypeDetailShowBinder(BaseActivity activity) {
        super(activity);
        clickCounter = new AtomicInteger();
        rideTypeActiveImageFactory = new RideTypeActiveImageFactory();
        setUpRideTypeClickStream();
    }

    private void setUpRideTypeClickStream() {
        rideTypeClicks = PublishSubject.create();
        rideTypeClicks.doOnNext(data -> clickCounter.incrementAndGet())
                .buffer(2, 1)
                .filter(this::wasRideTypeClickedTwice)
                .filter(this::hasTypeEstimates)
                .subscribe(this::showRideDetails, this::err, () -> {});
    }

    private void err(Throwable throwable) {
        throwable.printStackTrace();
    }

    private Boolean wasRideTypeClickedTwice(List<RideType> buf) {
        return buf.size() == 2 && buf.get(0).getProductId().equals(buf.get(1).getProductId());
    }

    private Boolean hasTypeEstimates(List<RideType> buf) {
        return !TextUtils.isEmpty(buf.get(1).getFareId());
    }

    private void showRideDetails(List<RideType> rideTypes) {
        RideType rideType = rideTypes.get(1);
        orderRideTypeDetailName.setText(rideType.getName());
        orderRideTypeDetailSlogan.setText(rideType.getSlogan());
        orderRideTypeDetailFare.setText(rideType.getFare());
        String capacityTemplate = getString(R.string.capacity_template);
        String preparedCapacity = String.format(capacityTemplate, rideType.getCapacity());
        orderRideTypeDetailCapacity.setText(preparedCapacity);
        String arrivalTime = calculateArrival(rideType);
        orderRideTypeDetailArrival.setText(arrivalTime);
        if (TextUtils.isEmpty(arrivalTime))
            orderRideTypeDetailArrivalTitle.setVisibility(View.INVISIBLE);
        else
            orderRideTypeDetailArrivalTitle.setVisibility(View.VISIBLE);
        int rideTypeImage = rideTypeActiveImageFactory.get(rideType.getName().toUpperCase());
        orderRideTypeDetailImage.setBackgroundResource(rideTypeImage);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private String calculateArrival(RideType rideType) {
        String pickupEstimate = rideType.getPickupEstimate();
        if (TextUtils.isEmpty(pickupEstimate) || "null".equals(pickupEstimate))
            return "";
        int rideDuration = Integer.parseInt(rideType.getDurationEstimate()) / 60;
        int pickupDuration = Integer.parseInt(pickupEstimate);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, rideDuration + pickupDuration);
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return dateFormat.format(instance.getTime());
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        bottomSheetBehavior = BottomSheetBehavior.from(orderRideTypeDetails);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        orderRideTypeDetails.setOnTouchListener((v, event) -> true);
    }

    @OnClick(R.id.order_ride_type_detail_close) void closeRideDetails() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderRideTypeWasChanged(OnRideTypeClicked event) {
        if (!TextUtils.isEmpty(event.getRideType().getFareId()))
            rideTypeClicks.onNext(event.getRideType());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderRideTypeWasChanged(ApplyAddressEvent event) {
        clickCounter.set(0);
        setUpRideTypeClickStream();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyFare(OrderRideTypeWasChanged event) {
        String activeCategoryName = getActiveCategoryName();
        if (activeCategoryName != null) {
            if (activeCategoryName.equals(event.getCategoryName()) && clickCounter.get() < 1)
                rideTypeClicks.onNext(event.getRideType());
        }
    }

    private String getActiveCategoryName() {
        int currentItem = orderRideTypeViewPager.getCurrentItem();
        Fragment item = ((RideTypeAdapter) orderRideTypeViewPager.getAdapter()).getItem(currentItem);
        Bundle arguments = item.getArguments();
        return arguments != null ? arguments.getString(OrderRideTypeFragment.ORDER_TYPE_ID) : null;
    }
}
