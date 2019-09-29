package app.gateway.bars.locations.near;

import android.location.Location;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import rx.Observable;

public interface GetNearLocationGateway {
    Observable<LocationsModel> sort(List<LocationsModel> locations, Location location);
}
