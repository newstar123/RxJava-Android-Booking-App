package app.core.location.blocker.interactor;

import android.location.Location;

import app.core.BaseInteractor;
import app.core.location.blocker.entity.LocationDemandRequest;
import app.core.location.blocker.entity.LocationsDemandResponse;
import app.core.location.get.GetRxLocationGateway;
import app.delivering.component.BaseActivity;
import app.gateway.location.blocker.LocationsDemandGateway;
import app.gateway.location.save.GetSharedLastLocationGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LocationDemandInteractor implements BaseInteractor<String, Observable<LocationsDemandResponse>> {

    private LocationsDemandGateway locationsDemandGateway;
    private GetRxLocationGateway getRxLocationGateway;

    public LocationDemandInteractor(BaseActivity activity) {
        getRxLocationGateway = new GetSharedLastLocationGateway(activity);
        locationsDemandGateway = QorumHttpClient.get().create(LocationsDemandGateway.class);
    }

    @Override
    public Observable<LocationsDemandResponse> process(String email) {
        return getRxLocationGateway.get()
                .observeOn(Schedulers.io())
                .map(location -> setUpRequest(location, email))
                .concatMap(requestParams -> locationsDemandGateway.post(requestParams));
    }

    private LocationDemandRequest setUpRequest(Location location, String email) {
        LocationDemandRequest request = new LocationDemandRequest();
        request.setEmail(email);
        request.setLatitude(location.getLatitude());
        request.setLongitude(location.getLongitude());
        return request;
    }
}
