package app.gateway.location.service;


import java.util.List;

import app.core.location.service.entity.ServiceLocation;
import app.core.location.service.gateway.ServiceLocationsGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RestServiceLocationsGateway implements ServiceLocationsGateway {
    private final ServiceLocationsGateway serviceLocationsGateway;

    public RestServiceLocationsGateway() {
        serviceLocationsGateway = QorumHttpClient.get().create(ServiceLocationsGateway.class);
    }

    @Override public Observable<List<ServiceLocation>> get() {
        return serviceLocationsGateway.get().observeOn(Schedulers.io());
    }
}
