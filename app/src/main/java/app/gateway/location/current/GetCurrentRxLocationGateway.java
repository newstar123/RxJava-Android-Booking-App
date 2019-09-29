
package app.gateway.location.current;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import app.core.location.get.GetRxLocationGateway;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

public class GetCurrentRxLocationGateway implements GetRxLocationGateway {
    private Context context;

    public GetCurrentRxLocationGateway(Context context) {
        this.context = context;
    }

    @Override public Observable<Location> get() {
        return Observable
                .concat(getLastKnown(context), observableLocation(context))
                .first();
    }

    private Observable<Location> getLastKnown(Context context) {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        return locationProvider.getLastKnownLocation();
    }

    private Observable<Location> observableLocation(Context context) {
        LocationRequest locationRequest = createLocationRequest();
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        return locationProvider.getUpdatedLocation(locationRequest);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }
}
