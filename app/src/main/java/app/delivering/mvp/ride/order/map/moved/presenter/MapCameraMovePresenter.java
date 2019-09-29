package app.delivering.mvp.ride.order.map.moved.presenter;


import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import app.core.location.geocode.bylocation.entity.GeocodeByLocationResponse;
import app.core.location.geocode.bylocation.interactor.GeocodeByLocationInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.map.moved.model.MapCameraMovedModel;
import rx.Observable;

public class MapCameraMovePresenter extends BasePresenter<LatLng, Observable<MapCameraMovedModel>>{
    private final GeocodeByLocationInteractor geocodeByLocationInteractor;

    public MapCameraMovePresenter(BaseActivity activity) {
        super(activity);
        geocodeByLocationInteractor = new GeocodeByLocationInteractor(activity);
    }

    @Override public Observable<MapCameraMovedModel> process(LatLng latLng) {
        return geocodeByLocationInteractor.process(latLng).map(this::convert);
    }

    private MapCameraMovedModel convert(GeocodeByLocationResponse response) {
        MapCameraMovedModel mapCameraMovedModel = new MapCameraMovedModel();
        Address address = response.getAddress();
        String addressLine = getAddressLine(address);
        mapCameraMovedModel.setAddressLine(addressLine);
        mapCameraMovedModel.setAddress(address);
        return mapCameraMovedModel;
    }

    private String getAddressLine(Address address) {
        if (address.getMaxAddressLineIndex() > 1)
            return address.getAddressLine(1) + ", " + address.getAddressLine(0);
        else if (address.getAddressLine(0) != null)
            return address.getAddressLine(0);
        return "";
    }


}
