package app.core.location.geocode.bylocation.interactor;


import com.google.android.gms.maps.model.LatLng;

import app.core.BaseInteractor;
import app.core.location.geocode.bylocation.entity.GeocodeByLocationResponse;
import app.core.location.geocode.bylocation.gateway.GeocodeByLocationRxGateway;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.location.place.bylocation.GeocodeByLocationRxLibGateway;
import rx.Observable;

public class GeocodeByLocationInteractor
        implements BaseInteractor<LatLng, Observable<GeocodeByLocationResponse>> {
    private final PermissionInteractor permissionInteractor;
    private final GeocodeByLocationRxGateway geocodeByLocationRxGateway;

    public GeocodeByLocationInteractor(BaseActivity activity) {
        permissionInteractor = new PermissionInteractor(activity);
        geocodeByLocationRxGateway = new GeocodeByLocationRxLibGateway();
    }

    @Override public Observable<GeocodeByLocationResponse> process(LatLng  latLng) {
        return permissionInteractor.process()
                .concatMap(isGranted -> geocodeByLocationRxGateway.get(latLng))
                .map(GeocodeByLocationResponse::new);
    }


}
