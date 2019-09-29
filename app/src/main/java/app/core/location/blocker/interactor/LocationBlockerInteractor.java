package app.core.location.blocker.interactor;


import app.core.BaseOutputInteractor;
import app.core.location.blocker.entity.LocationsDemandRadiusResponse;
import app.gateway.location.blocker.DemandRadiusGateway;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LocationBlockerInteractor implements BaseOutputInteractor<Observable<LocationsDemandRadiusResponse>> {

    private final DemandRadiusGateway demandRadiusGateway;

    public LocationBlockerInteractor() {
        demandRadiusGateway = QorumHttpClient.get().create(DemandRadiusGateway.class);
    }

    @Override
    public Observable<LocationsDemandRadiusResponse> process() {
        return demandRadiusGateway.get()
                .observeOn(Schedulers.io());
    }
}
