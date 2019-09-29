package app.gateway.bars.locations.cache.put;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import app.gateway.bars.locations.cache.holder.LocationsListRealTimeHolder;
import rx.Observable;

public class PutLocationsListInRealTimeGateway implements PutLocationsInRealTimeGateway{
    @Override public Observable<List<LocationsModel>> put(List<LocationsModel> list) {
        return LocationsListRealTimeHolder.setList(list);
    }
}
