package app.delivering.mvp.ride.order.advert.image.binder;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.advert.entity.AdvertAttributes;
import app.core.ride.delayed.discount.model.DiscountUpdatesModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.tab.advert.presenter.AdvertImagePresenter;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class ImageAdvertBinder extends BaseBinder {
    @BindView(R.id.order_ride_advert) ImageView orderRideAdvertImageView;
    private final AdvertImagePresenter advertImagePresenter;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<DiscountUpdatesModel> replaySubject;
    private double discount;

    public ImageAdvertBinder(BaseActivity activity) {
        super(activity);
        advertImagePresenter = new AdvertImagePresenter(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        advertImagePresenter.process(discount)
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OnOrderRideStartEvent event) {
        if (hasToRestore())
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void show(DiscountUpdatesModel advertResponse) {
        Picasso.with(getActivity())
                .load(setUpDiscountLogic(advertResponse))
                .error(R.drawable.inset_broken_resource)
                .into(orderRideAdvertImageView);
    }

    private String setUpDiscountLogic(DiscountUpdatesModel discountUpdatesModel) {
        int rideTypeKey = getActivity().getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        InitialRideType rideType = InitialRideType.toType(rideTypeKey);
        AdvertAttributes advertModel = discountUpdatesModel.getAdvertResponse().getData().getAttributes();

        return rideType == InitialRideType.TO_THE_VENUE ? advertModel.getUberToVenue() :
                discountUpdatesModel.isEligible() ? advertModel.getUberFromVenueEligible() :
                        advertModel.getUberFromVenueNotEligible();
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> afterViewsBounded());
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void resetState() {
        replaySubject = ReplaySubject.create();
        onStartActivity(new OnOrderRideStartEvent());
    }
}
