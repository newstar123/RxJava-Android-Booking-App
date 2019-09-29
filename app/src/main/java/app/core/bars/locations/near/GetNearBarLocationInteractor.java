package app.core.bars.locations.near;

import android.location.Location;

import java.util.List;

import app.core.BaseInteractor;
import app.core.bars.locations.entity.LocationsModel;
import app.core.bars.locations.entity.exceptions.TooFarLocationException;
import app.core.location.blocker.interactor.LocationBlockerInteractor;
import app.core.location.get.GetRxLocationInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.bars.locations.near.GetNearBarLocationGateway;
import app.gateway.bars.locations.near.GetNearLocationGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetNearBarLocationInteractor implements BaseInteractor<List<LocationsModel>, Observable<LocationsModel>> {
    private final GetRxLocationInteractor getRxLocationInteractor;
    private GetNearLocationGateway nearLocationGateway;
    private LocationBlockerInteractor blockerInteractor;

    public GetNearBarLocationInteractor(BaseActivity activity) {
        getRxLocationInteractor = new GetRxLocationInteractor(activity);
        nearLocationGateway = new GetNearBarLocationGateway();
        blockerInteractor = new LocationBlockerInteractor();
    }

    @Override public Observable<LocationsModel> process(List<LocationsModel> models) {
        return findNearestLocationImageUrl(models);
    }

    private Observable<LocationsModel> getNearLocationWithDemandRadius(List<LocationsModel> locationsModels, Location location) {
        return Observable.zip(nearLocationGateway.sort(locationsModels, location),
                blockerInteractor.process(),
                (model, locationsDemandResponse) -> {
            Location nearestMarketLocation = new Location("nearestMarketLocation");
            nearestMarketLocation.setLongitude(model.getLongitude());
            nearestMarketLocation.setLatitude(model.getLatitude());
            float currDistance = location.distanceTo(nearestMarketLocation);

            if ((int) currDistance > locationsDemandResponse.getResult())
                throw new TooFarLocationException(model.getImageUrl());

            return model;
        });
    }

    private Observable<LocationsModel> findNearestLocationImageUrl(List<LocationsModel> locationsModels) {
        return getRxLocationInteractor.process()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(location -> getNearLocationWithDemandRadius(locationsModels, location));
    }
}
