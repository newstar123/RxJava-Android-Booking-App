package app.delivering.mvp.ride.order.route.apply.vendor.binder;

import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
import app.core.location.route.entity.RoutePlaceByIdToBarRequest;
import app.core.location.route.entity.RoutePlaceByIdToBarResponse;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.ride.order.address.apply.standard.events.ApplyAddressEvent;
import app.delivering.mvp.ride.order.animation.binder.OrderRideAnimation;
import app.delivering.mvp.ride.order.fare.apply.events.OnOrderRideStartEvent;
import app.delivering.mvp.ride.order.fare.apply.events.RemoveLastFareEvent;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.init.frombar.binder.FromBarInitBinder;
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyRoutesException;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder.FocusOnMapSubBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder.ShowMarkersOnMapFromBarSubBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder.ShowMarkersOnMapToBarSubBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.events.ClearMapEvent;
import app.delivering.mvp.ride.order.route.apply.vendor.presenter.ApplyRoutePresenter;
import app.delivering.mvp.ride.order.type.apply.events.ApplyTypesEvent;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;


public class ApplyRouteBinder extends BaseBinder {
    private final ShowMarkersOnMapToBarSubBinder showMarkersOnMapToBarSubBinder;
    private final ShowMarkersOnMapFromBarSubBinder showMarkersOnMapFromBarSubBinder;
    @BindView(R.id.order_ride_map) MapView mapView;
    @BindView(R.id.order_ride_bar) TextView orderRideBarTextView;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    @BindView(R.id.order_ride_commit_ride_type) TextView orderRideCommitRideTypeTextView;
    private final ApplyRoutePresenter applyRoutePresenter;
    private final FocusOnMapSubBinder focusOnMapSubBinder;
    private ReplaySubject<RoutePlaceByIdToBarResponse> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    private final RxDialogHandler dialogHandler;
    private Place place;

    public ApplyRouteBinder(BaseActivity activity) {
        super(activity);
        applyRoutePresenter = new ApplyRoutePresenter(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        focusOnMapSubBinder = new FocusOnMapSubBinder(activity);
        showMarkersOnMapToBarSubBinder = new ShowMarkersOnMapToBarSubBinder(getActivity());
        showMarkersOnMapFromBarSubBinder = new ShowMarkersOnMapFromBarSubBinder(getActivity());
        dialogHandler = new RxDialogHandler(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderRideTypeWasChanged(ApplyAddressEvent event) {
        if (getRideType() != InitialRideType.CUSTOM) {
            this.place = event.getModel();
            createRoute(event.getModel());
        }
    }

    private void createRoute(Place place) {
        RoutePlaceByIdToBarRequest request = createRequest(place);
        applyRoutePresenter.process(request).subscribe(replaySubject);
    }

    private RoutePlaceByIdToBarRequest createRequest(Place place) {
        RoutePlaceByIdToBarRequest request = new RoutePlaceByIdToBarRequest();
        switch (getRideType()) {
            case FROM_THE_VENUE:
                LatLng barLatLng = (LatLng) orderRideBarTextView.getTag();
                request.setDestination(place.getLatLng());
                request.setDeparture(barLatLng);
                break;
            default:
                LatLng destLatLng = (LatLng) orderRideBarTextView.getTag();
                request.setDestination(destLatLng);
                request.setDeparture(place.getLatLng());
        }
        return request;
    }

    private InitialRideType getRideType() {
        int value = getActivity().getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClearMapEvent(ClearMapEvent event) {
        clearMarkers();
        MapObservable.get(mapView).subscribe(GoogleMap::clear, err -> {});
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

    private void clearMarkers() {
        showMarkersOnMapToBarSubBinder.clear();
        showMarkersOnMapFromBarSubBinder.clear();
    }

    private void show(RoutePlaceByIdToBarResponse response) {
        OrderRideAnimation.run(getActivity());
        List<LatLng> route = response.getRoute();
        mapView.setTag(route);
        resetState();
        MapObservable.get(mapView).subscribe(GoogleMap::clear, err -> {});

        //clear information about last request car type
        orderRideCommitRideTypeTextView.setTag(new RideType());

        focusOnMapSubBinder.apply(mapView, route);
        applyMapMarkers(route);
        EventBus.getDefault().post(new ApplyTypesEvent(route));
    }

    private void applyMapMarkers(List<LatLng> route) {
        switch (FromBarInitBinder.getRideType(getActivity())) {
            case FROM_THE_VENUE:
                showMarkersOnMapFromBarSubBinder.apply(mapView, route);
                break;
            default:
                showMarkersOnMapToBarSubBinder.apply(mapView, route);
        }
    }

    private void showError(Throwable throwable) {
        resetState();
        if (throwable instanceof EmptyRoutesException) {
            EventBus.getDefault().post(new ClearMapEvent());
            dialogHandler.showOneButtonWithoutTitle(R.string.route_error, R.string.ok)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(isOk -> {
                        if (isOk)
                            dialogHandler.dismissDialog();
                        clearMarkers();
                        EventBus.getDefault().post(new RemoveLastFareEvent());
                    }, this::showError, () -> {});
        } else
            initExceptionHandler.showError(throwable, v -> createRoute(place));
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
