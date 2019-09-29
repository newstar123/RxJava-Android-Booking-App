package app.gateway.location.save;

import android.content.Context;
import android.location.Location;

import app.core.location.put.PutLocationGateway;
import app.gateway.shared.base.BaseTextCacheLong;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class PutSharedLastLocationGateway implements PutLocationGateway {
    private final BaseTextCacheLong locationLatitudeCache;
    private final BaseTextCacheLong locationLongitudeCache;

    public PutSharedLastLocationGateway(Context context) {
        locationLatitudeCache = new BaseTextCacheLong(context, "app.gateway.location.save.location.LATITUDE");
        locationLongitudeCache = new BaseTextCacheLong(context, "app.gateway.location.save.location.LONGITUDE");
    }

    @Override public Observable<Location> put(Location location) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override public void call(Subscriber<? super Location> subscriber) {
                locationLatitudeCache.save((long) location.getLatitude());
                locationLongitudeCache.save((long) location.getLongitude());
                subscriber.onNext(location);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
