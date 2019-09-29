package app.delivering.mvp.ride.order.type.pool.capacity.change.binder;


import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.product.entity.UberProductResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.apply.model.ApplyTypesModel;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.RideCategoryFactory;
import app.delivering.mvp.ride.order.type.pool.capacity.change.events.OnChangePoolCapacityEvent;
import app.delivering.mvp.ride.order.type.pool.capacity.change.presenter.ChangePoolCapacityPresenter;
import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class ChangePoolCapacityBinder extends BaseBinder {
    private final InitExceptionHandler initExceptionHandler;
    private final ChangePoolCapacityPresenter changePoolCapacityPresenter;
    private ReplaySubject<RideType> replaySubject;
    @BindView(R.id.order_ride_commit_ride_type) TextView commitRideTypeTextView;
    @BindView(R.id.order_ride_pool_fare) TextView poolFareTextView;
    @BindView(R.id.order_ride_pool_capacity_1) RadioButton poolCapacity1View;
    @BindView(R.id.order_ride_pool_capacity_2) RadioButton poolCapacity2View;
    @BindView(R.id.order_ride_type_pager) ViewPager orderRideTypeViewPager;
    @BindView(R.id.order_ride_map) MapView mapView;
    private OnChangePoolCapacityEvent event;
    private Subscription poolCapacity2Subscribe;

    public ChangePoolCapacityBinder(BaseActivity activity) {
        super(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        changePoolCapacityPresenter = new ChangePoolCapacityPresenter(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePoolCapacity(OnChangePoolCapacityEvent event) {
        this.event = event;
        poolFareTextView.setText("");
        if (event.getCheckedId() == R.id.order_ride_pool_capacity_1)
            showPoolCapacity1();
        else
            showPoolCapacity2();
    }

    private void showPoolCapacity1() {
        if (poolCapacity2Subscribe != null && !poolCapacity2Subscribe.isUnsubscribed())
            poolCapacity2Subscribe.unsubscribe();
        poolCapacity2View.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        poolCapacity1View.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        RideType rideType = (RideType) poolCapacity1View.getTag();
        poolFareTextView.setText(rideType.getFare());
        commitRideTypeTextView.setTag(rideType);
    }

    private void showPoolCapacity2() {
        Object tag = poolCapacity2View.getTag();
        poolCapacity2View.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        poolCapacity1View.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        if (tag == null)
            getPoolCapacity2Fare();
        else{
            poolFareTextView.setText(((RideType) tag).getFare());
            commitRideTypeTextView.setTag(tag);
        }
    }

    @SuppressWarnings("unchecked") private void getPoolCapacity2Fare() {
        PostUberEstimateRequest postUberEstimateRequest = new PostUberEstimateRequest();
        List<LatLng> route = (List<LatLng>) mapView.getTag();
        postUberEstimateRequest.setDestination(route.get(route.size() - 1));
        postUberEstimateRequest.setDeparture(route.get(0));
        ApplyTypesModel typeModel = (ApplyTypesModel) orderRideTypeViewPager.getTag();
        UberProductResponse pool = Observable.from(typeModel.getProducts().getProducts())
                .filter(product -> product.getDisplayName().equals(RideCategoryFactory.POOL))
                .toBlocking()
                .first();
        postUberEstimateRequest.setProduct(pool);
        postUberEstimateRequest.setCapacity(2);
        showProgress();
         poolCapacity2Subscribe = changePoolCapacityPresenter.process(postUberEstimateRequest)
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

    private void show(RideType rideType) {
        resetState();
        poolCapacity2View.setTag(rideType);
        commitRideTypeTextView.setTag(rideType);
        poolFareTextView.setText(rideType.getFare());
    }

    private void showError(Throwable throwable) {
        resetState();
        initExceptionHandler.showError(throwable, v -> onChangePoolCapacity(event));
    }

    private void progressState() {
        showProgress();
        initExceptionHandler.dismiss();
    }

    private void resetState() {
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartActivity(new OnOrderRideStartEvent());
    }
}
