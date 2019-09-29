package app.gateway.bars.locations.cache.get;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import app.gateway.bars.locations.cache.holder.LocationsListRealTimeHolder;
import rx.Observable;

public class GetLocationListRealTimeGateway implements GetLocationsListInRealTimeGateway {

    @Override public Observable<List<LocationsModel>> get() {
        return LocationsListRealTimeHolder.getList();
    }
}
