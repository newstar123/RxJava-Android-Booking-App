package app.gateway.bars.locations.cache.put;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import rx.Observable;

public interface PutLocationsInRealTimeGateway {
    Observable<List<LocationsModel>> put(List<LocationsModel> list);
}
