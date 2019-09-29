package app.delivering.mvp.advert.ride.start.binder.subbinder;

import app.ApplicationConfig;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class RideTrackingServiceSubBinder {

    public static void launch() {
        Observable.just((String)QorumSharedCache.checkUberRideId().get(BaseCacheType.STRING))
                .subscribe(RideTrackingServiceSubBinder::launch);
    }

    public static void launch(String rideId){
        ApplicationConfig.launchMockRide(rideId);
    }
}
