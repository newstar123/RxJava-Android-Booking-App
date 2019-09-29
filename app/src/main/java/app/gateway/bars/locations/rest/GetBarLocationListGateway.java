package app.gateway.bars.locations.rest;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetBarLocationListGateway implements GetBarLocationsGateway {
    private final GetBarLocationsGateway gateway;

    public GetBarLocationListGateway() {
        gateway = QorumHttpClient.get().create(GetBarLocationsGateway.class);
    }

    @Override public Observable<List<LocationsModel>> get() {
        return gateway.get().subscribeOn(Schedulers.io());
    }

}
