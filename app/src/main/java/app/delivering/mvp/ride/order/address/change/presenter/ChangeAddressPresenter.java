package app.delivering.mvp.ride.order.address.change.presenter;

import com.google.android.gms.location.places.Place;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.gateway.location.place.byplaceid.LocationByPlaceRxLibGateway;
import rx.Observable;

public class ChangeAddressPresenter extends BasePresenter<String, Observable<Place>> {
    private final LocationByPlaceRxLibGateway locationByPlaceIdGateway;

    public ChangeAddressPresenter(BaseActivity activity) {
        super(activity);
        locationByPlaceIdGateway = new LocationByPlaceRxLibGateway();
    }

    @Override
    public Observable<Place> process(String placeId) {
        return locationByPlaceIdGateway.get(placeId);
    }
}
