package app.delivering.mvp.ride.order.fare.apply.binder;


import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.BuildConfig;
import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import app.delivering.mvp.ride.order.fare.apply.events.ApplyFareEvent;
import app.delivering.mvp.ride.order.fare.apply.model.OnOrderRideFareReset;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.fare.apply.events.RemoveLastFareEvent;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import app.delivering.mvp.ride.order.fare.apply.presenter.ApplyFarePresenter;
import app.delivering.mvp.ride.order.route.apply.vendor.events.ClearMapEvent;
import app.delivering.mvp.ride.order.type.update.events.UpdateTypeWithFareEvent;
import butterknife.BindView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class ApplyFareBinder extends BaseBinder {
    private final ApplyFarePresenter applyFarePresenter;
    private final InitExceptionHandler initExceptionHandler;
    private ReplaySubject<List<RideCategory>> replaySubject;
    private List<ApplyFareEvent> eventQueue;
    @BindView(R.id.order_ride_pickup_estimate) TextView orderRidePickupEstimateTextView;
    private ApplyFareEvent event;

    public ApplyFareBinder(BaseActivity activity) {
        super(activity);
        applyFarePresenter = new ApplyFarePresenter(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        eventQueue = new ArrayList<>();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApplyFare(ApplyFareEvent event) {
        this.event = event;
        Object tag = orderRidePickupEstimateTextView.getTag();
        if (tag == null)
            getFates();
        else
            showResult((List<RideCategory>) tag);
    }

    private void getFates() {
        if (!hasToRestore()) {
            progressState();
            OnOrderRideFareReset.send();
            OnOrderRideFareReset.subscribe = Observable.interval(0, 90, TimeUnit.SECONDS)
                    .concatMap(i -> applyFarePresenter.process(event))
                    .subscribe(replaySubject);
        } else
            eventQueue.add(event);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoveLastFare(RemoveLastFareEvent event) {
        if (!eventQueue.isEmpty())
            eventQueue.remove(eventQueue.size() -1);
    }

    private void show(List<RideCategory> rideCategories) {
        resetState();
        if (eventQueue.isEmpty())
            showResult(rideCategories);
        else
            repeat();
    }

    private void repeat() {
        ApplyFareEvent lastEvent = eventQueue.get(eventQueue.size() - 1);
        onApplyFare(lastEvent);
        eventQueue = new ArrayList<>();
    }

    private void showResult(List<RideCategory> rideCategories) {
        resetState();
        OrderRideAnimation.run(getActivity());
        orderRidePickupEstimateTextView.setTag(rideCategories);
        orderRidePickupEstimateTextView.setVisibility(View.VISIBLE);
        EventBus.getDefault().postSticky(new UpdateTypeWithFareEvent(rideCategories));
    }

    private void showError(Throwable throwable) {
        resetState();
        if (throwable instanceof HttpException && ((HttpException) throwable).code() == 429 && BuildConfig.DEBUG)
            Toast.makeText(getActivity(), "Exceeded the limit of test travel requests", Toast.LENGTH_SHORT).show();
        else if ((throwable instanceof HttpException && ((HttpException) throwable).code() == 422))
            EventBus.getDefault().post(new ClearMapEvent());
        else {
            initExceptionHandler.showError(throwable, v -> onApplyFare(event));
        }
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
