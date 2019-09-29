package app.delivering.mvp.advert.ride.start.binder;


import android.app.Activity;
import android.content.Intent;

import com.uber.sdk.rides.client.model.Ride;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.atomic.AtomicInteger;

import app.CustomApplication;
import app.core.uber.start.entity.RideRequestInvalidException;
import app.core.uber.start.entity.StartUberRideRequest;
import app.core.uber.start.entity.StartUberRideResponse;
import app.core.uber.start.interactor.StartUberRideInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.advert.VideoAdvertActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.component.service.order.DeleteOrderService;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.advert.progress.events.OnStartUberRideEvent;
import app.delivering.mvp.advert.ride.delete.events.OnApplyPromoErrorEvent;
import app.delivering.mvp.advert.ride.start.binder.subbinder.PrepareRideDirection;
import app.delivering.mvp.advert.ride.start.binder.subbinder.RideTrackingServiceSubBinder;
import app.delivering.mvp.advert.ride.start.binder.subbinder.StartUberAppSubBinder;
import app.delivering.mvp.advert.ride.start.events.OnCancelOrderEvent;
import app.delivering.mvp.advert.ride.start.events.ShowRideRequestResultEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class StartRideBinder extends BaseBinder {
    private final StartUberRideInteractor startUberRideInteractor;
    private final InitExceptionHandler initExceptionHandler;
    private final StartUberAppSubBinder startUberAppSubBinder;
    private ReplaySubject<StartUberRideResponse> replaySubject;
    private AtomicInteger cancelCounter;

    public StartRideBinder(BaseActivity activity) {
        super(activity);
        startUberRideInteractor = new StartUberRideInteractor(activity);
        replaySubject = ReplaySubject.create();
        initExceptionHandler = new InitExceptionHandler(getActivity());
        cancelCounter = new AtomicInteger();
        startUberAppSubBinder = new StartUberAppSubBinder(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartUberRide(OnStartUberRideEvent event) {
        showProgress();
        StartUberRideRequest startUberRideRequest = fillStartRideUberRequest();
        startUberRideInteractor
                .process(startUberRideRequest)
                .concatMap(this::checkRideManualCancel)
                .retryWhen(this::checkRideManualCancelDuringPromocodeError)
                .subscribe(replaySubject);
    }

    private StartUberRideRequest fillStartRideUberRequest() {
        StartUberRideRequest startUberRideRequest = new StartUberRideRequest();
        Intent intent = getActivity().getIntent();
        startUberRideRequest.setProductId(intent.getStringExtra(VideoAdvertActivity.PRODUCT_ID_KEY));
        startUberRideRequest.setFareId(intent.getStringExtra(VideoAdvertActivity.FARE_ID_KEY));
        startUberRideRequest.setFareExpiredAt(intent.getLongExtra(VideoAdvertActivity.FARE_EXPIRED_AT_KEY, 0));
        startUberRideRequest.setCapacity(intent.getIntExtra(VideoAdvertActivity.CAPACITY_KEY, 0));
        startUberRideRequest.setDepartureAddress(intent.getStringExtra(VideoAdvertActivity.DEPARTURE_ADDRESS_KEY));
        startUberRideRequest.setDestinationAddress(intent.getStringExtra(VideoAdvertActivity.DESTINATION_ADDRESS_KEY));
        startUberRideRequest.setDeparture(intent.getParcelableExtra(VideoAdvertActivity.DEPARTURE_KEY));
        startUberRideRequest.setRideDirection(PrepareRideDirection.get(getActivity(), VideoAdvertActivity.RIDE_DIRECTION_KEY));
        startUberRideRequest.setDestination(intent.getParcelableExtra(VideoAdvertActivity.DESTINATION_KEY));
        double discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        startUberRideRequest.setDiscount(discount);
        return startUberRideRequest;
    }

    private Observable<?> checkRideManualCancelDuringPromocodeError(Observable<? extends Throwable> errorObservable) {
        return errorObservable.concatMap(error -> {
            if (!(error instanceof RideRequestInvalidException))
                return checkRideManualCancel(new StartUberRideResponse(new Ride()))
                        .concatMap(result -> Observable.error(error));
            return Observable.error(error);
        });
    }

    private Observable<StartUberRideResponse> checkRideManualCancel(StartUberRideResponse result) {
        return Observable.just(result)
                .doOnNext(r -> startDeleteRideService())
                .doOnNext(r -> cancelCounter.incrementAndGet());
    }

    private void startDeleteRideService() {
        Intent intent = new Intent(CustomApplication.get(), DeleteOrderService.class);
        if (cancelCounter.get() > 0)
            CustomApplication.get().startService(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowRideRequestResult(ShowRideRequestResultEvent event) {
        replaySubject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::show, this::showError);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCancelOrder(OnCancelOrderEvent event) {
        if (cancelCounter.get() > 0)
            startDeleteRideService();
        cancelCounter.incrementAndGet();
    }

    private void show(StartUberRideResponse startUberRideResponse) {
        hideProgress();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
        startRideFlow(startUberRideResponse);
    }

    private void startRideFlow(StartUberRideResponse startUberRideResponse) {
        startTrackingService(startUberRideResponse);
        startUberAppSubBinder.start();
    }

    private void startTrackingService(StartUberRideResponse startUberRideResponse) {
        String rideId = startUberRideResponse.getBody().getRideId();
        RideTrackingServiceSubBinder.launch(rideId);
    }

    private void showError(Throwable throwable) {
        hideProgress();
        throwable.printStackTrace();
        if (throwable instanceof RideRequestInvalidException)
            rideFailError(throwable);
        else
            applyPromoError();
    }

    private void rideFailError(Throwable throwable) {
        replaySubject = ReplaySubject.create();
        initExceptionHandler.showError(throwable, v -> retry());
    }

    private void applyPromoError() {
        RideTrackingServiceSubBinder.launch();
        EventBus.getDefault().post(new OnApplyPromoErrorEvent());
    }

    private void retry() {
        afterViewsBounded();
        onShowRideRequestResult(new ShowRideRequestResultEvent());
    }

}
