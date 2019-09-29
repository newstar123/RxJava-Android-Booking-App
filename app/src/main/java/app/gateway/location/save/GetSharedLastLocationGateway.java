package app.gateway.location.save;

import android.content.Context;
import android.location.Location;

import app.core.location.get.GetRxLocationGateway;
import app.gateway.shared.base.BaseTextCacheLong;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GetSharedLastLocationGateway implements GetRxLocationGateway {
    private static final String PROVIDER = "sharedpreferences_provider";
    private final BaseTextCacheLong locationLatitudeCache;
    private final BaseTextCacheLong locationLongitudeCache;

    public GetSharedLastLocationGateway(Context context) {
        locationLatitudeCache = new BaseTextCacheLong(context, "app.gateway.location.save.location.LATITUDE");
        locationLongitudeCache = new BaseTextCacheLong(context, "app.gateway.location.save.location.LONGITUDE");
    }

    @Override public Observable<Location> get() {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override public void call(Subscriber<? super Location> subscriber) {
                double latitude = locationLatitudeCache.get();
                double longitude = locationLongitudeCache.get();
                Location location = new Location(PROVIDER);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                subscriber.onNext(location);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
