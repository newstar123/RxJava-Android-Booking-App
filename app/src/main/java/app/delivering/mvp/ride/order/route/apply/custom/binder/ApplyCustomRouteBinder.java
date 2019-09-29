package app.delivering.mvp.ride.order.route.apply.custom.binder;

import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import app.R;
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
import app.delivering.mvp.ride.order.init.type.InitialRideType;
import app.delivering.mvp.ride.order.route.apply.custom.model.ApplyCustomPlaceModel;
import app.delivering.mvp.ride.order.route.apply.custom.events.ApplyPickUpAddressEvent;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyDeparturePointException;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyDestinationPointException;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyRoutesException;
import app.delivering.mvp.ride.order.route.apply.custom.presenter.GetCustomRoutePresenter;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.map.MapObservable;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder.FocusOnMapSubBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.binder.subbinder.ShowMarkersOnMapDeferredRideSubBinder;
import app.delivering.mvp.ride.order.route.apply.vendor.events.ClearMapEvent;
import app.delivering.mvp.ride.order.type.apply.events.ApplyTypesEvent;
import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class ApplyCustomRouteBinder extends BaseBinder {
    private final ShowMarkersOnMapDeferredRideSubBinder showMarkersOnMapDeferredRideSubBinder;
    @BindView(R.id.order_ride_map) MapView mapView;
    @BindView(R.id.order_ride_bar) TextView orderRideBarTextView;
    @BindView(R.id.order_pick_up_address) EditText orderPickUpAddress;
    @BindView(R.id.order_ride_address) EditText orderRideAddressEditText;
    private final FocusOnMapSubBinder focusOnMapSubBinder;
    private ReplaySubject<RoutePlaceByIdToBarResponse> replaySubject;
    private final InitExceptionHandler initExceptionHandler;
    private final GetCustomRoutePresenter getCustomRoutePresenter;
    private final RxDialogHandler dialogHandler;
    private Place place;
    private boolean isDeparturePositionChanged;

    public ApplyCustomRouteBinder(BaseActivity activity) {
        super(activity);
        initExceptionHandler = new InitExceptionHandler(getActivity());
        replaySubject = ReplaySubject.create();
        focusOnMapSubBinder = new FocusOnMapSubBinder(activity);
        getCustomRoutePresenter = new GetCustomRoutePresenter(activity);
        showMarkersOnMapDeferredRideSubBinder = new ShowMarkersOnMapDeferredRideSubBinder(getActivity());
        dialogHandler = new RxDialogHandler(getActivity());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderRideTypeWasChanged(ApplyAddressEvent event) {
        if (getRideType() == InitialRideType.CUSTOM) {
            this.place = event.getModel();
            this.isDeparturePositionChanged = false;
            createRoute(event.getModel());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderDeparturePositionWasChanged(ApplyPickUpAddressEvent event) {
        this.place = event.getModel();
        this.isDeparturePositionChanged = true;
        createRoute(event.getModel());
    }

    private void createRoute(Place place) {
        ApplyCustomPlaceModel model = new ApplyCustomPlaceModel();
        model.setPlace(place);
        model.setDeparture((Place) orderRideAddressEditText.getTag());
        model.setDestination((Place) orderPickUpAddress.getTag());
        model.departure(isDeparturePositionChanged);
        getCustomRoutePresenter.process(model)
                .subscribe(replaySubject);
    }

    private InitialRideType getRideType() {
        int value = getActivity().getIntent().getIntExtra(OrderRideActivity.ORDER_RIDE_FROM_BAR_KEY, 0);
        return InitialRideType.toType(value);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClearMapEvent(ClearMapEvent event) {
        showMarkersOnMapDeferredRideSubBinder.clear();
        MapObservable.get(mapView).subscribe(GoogleMap::clear, err -> {});
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartActivity(OnOrderRideStartEvent event) {
        if (hasToRestore())
            progressState();
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError, () -> {});
    }

    private void show(RoutePlaceByIdToBarResponse response) {
        OrderRideAnimation.run(getActivity());
        List<LatLng> route = response.getRoute();
        mapView.setTag(route);
        resetState();
        MapObservable.get(mapView).subscribe(GoogleMap::clear, err -> {});
        focusOnMapSubBinder.apply(mapView, route);
        showMarkersOnMapDeferredRideSubBinder.apply(mapView, route);
        EventBus.getDefault().post(new ApplyTypesEvent(route));
    }

    private void showError(Throwable throwable) {
        resetState();
        if (throwable instanceof EmptyDeparturePointException) {
            refreshMapState();
            initExceptionHandler.showError(throwable, v -> orderPickUpAddress.performClick());
        } else if (throwable instanceof EmptyDestinationPointException) {
            refreshMapState();
            initExceptionHandler.showError(throwable, v -> orderRideBarTextView.performClick());
        } else if (throwable instanceof EmptyRoutesException) {
            EventBus.getDefault().post(new ClearMapEvent());
            dialogHandler.showOneButtonWithoutTitle(R.string.route_error, R.string.ok)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(isOk -> {
                        if (isOk)
                            dialogHandler.dismissDialog();
                        showMarkersOnMapDeferredRideSubBinder.clear();
                        EventBus.getDefault().post(new RemoveLastFareEvent());
                    }, this::showError, () -> {});
        } else
            initExceptionHandler.showError(throwable, v -> createRoute(place));
    }

    private void refreshMapState() {
        onClearMapEvent(new ClearMapEvent());
        MapObservable.get(mapView).subscribe(this::applyMarkers, err -> {});
    }

    private void applyMarkers(GoogleMap map) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
        if (isDeparturePositionChanged) {
            showMarkersOnMapDeferredRideSubBinder.addDeparture(map, place.getLatLng());
            orderRideAddressEditText.setTag(place);
        } else {
            showMarkersOnMapDeferredRideSubBinder.addDestination(map, place.getLatLng());
            orderPickUpAddress.setTag(place);
        }
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