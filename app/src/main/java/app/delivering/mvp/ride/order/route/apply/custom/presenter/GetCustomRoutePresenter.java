package app.delivering.mvp.ride.order.route.apply.custom.presenter;

import com.google.android.gms.location.places.Place;

import app.core.location.route.entity.RoutePlaceByIdToBarRequest;
import app.core.location.route.entity.RoutePlaceByIdToBarResponse;
import app.core.location.route.interactor.RoutePlaceByIdToBarInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.route.apply.custom.model.ApplyCustomPlaceModel;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyDeparturePointException;
import app.delivering.mvp.ride.order.route.apply.custom.exceptions.EmptyDestinationPointException;
import rx.Observable;

public class GetCustomRoutePresenter extends BasePresenter<ApplyCustomPlaceModel, Observable<RoutePlaceByIdToBarResponse>>{
    private final RoutePlaceByIdToBarInteractor interactor;


    public GetCustomRoutePresenter(BaseActivity activity) {
        super(activity);
        interactor = new RoutePlaceByIdToBarInteractor(activity);
    }

    @Override public Observable<RoutePlaceByIdToBarResponse> process(ApplyCustomPlaceModel model) {
        return getRequest(model)
                .concatMap(interactor::process);
    }

    private Observable<RoutePlaceByIdToBarRequest> getRequest(ApplyCustomPlaceModel model) {
        return Observable.create(subscriber -> {
            Place departure = model.getDeparture();
            if (departure == null)
                subscriber.onError(new EmptyDeparturePointException());
            Place destination = model.getDestination();
            if (destination == null)
                subscriber.onError(new EmptyDestinationPointException());
            RoutePlaceByIdToBarRequest request = new RoutePlaceByIdToBarRequest();
            if (model.isDeparture()) {
                request.setDestination(departure.getLatLng());
                request.setDeparture(model.getPlace().getLatLng());
            } else {
                request.setDestination(model.getPlace().getLatLng());
                request.setDeparture(destination.getLatLng());
            }
            subscriber.onNext(request);
            subscriber.onCompleted();
        });
    }

}
