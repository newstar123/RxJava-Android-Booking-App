package app.gateway.bars.locations.get;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import rx.Observable;

public interface GetCommonLocationsGateway {
    Observable<List<LocationsModel>> get();
}
