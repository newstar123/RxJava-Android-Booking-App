package app.core.uber.tracking.to.bar.interactor;

import android.content.Context;

import app.core.BaseInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.mvp.push.ride.complete.to.events.PushRideToBarCompleteEvent;
import app.gateway.gcm.push.GetParsedPushBarModelGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class EndRideToBarTrackingInteractor implements BaseInteractor<PushRideToBarCompleteEvent, Observable<BarModel>> {
    private final GetParsedPushBarModelGateway getParsedPushBarModelGateway;

    public EndRideToBarTrackingInteractor(Context context) {
        getParsedPushBarModelGateway = new GetParsedPushBarModelGateway(context);
    }

    @Override public Observable<BarModel> process(PushRideToBarCompleteEvent event) {
        return Observable.just(QorumSharedCache.checkUberRideId().save(BaseCacheType.STRING, ""))
                .concatMap(s -> getParsedPushBarModelGateway.parse(event.getMessage()));
    }
}
