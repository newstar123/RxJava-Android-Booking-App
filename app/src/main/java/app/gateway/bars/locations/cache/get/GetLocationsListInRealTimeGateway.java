package app.gateway.bars.locations.cache.get;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import rx.Observable;

public interface GetLocationsListInRealTimeGateway {
    Observable<List<LocationsModel>> get();
}
