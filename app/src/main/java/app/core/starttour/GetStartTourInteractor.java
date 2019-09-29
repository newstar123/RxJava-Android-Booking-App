package app.core.starttour;

import android.content.Context;

import app.core.BaseOutputInteractor;
import app.core.starttour.entity.StartTourShowingError;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetStartTourInteractor implements BaseOutputInteractor<Observable<Integer>> {
    private static final int START_TOUR_SCREENS_NUMBER = 4;

    public GetStartTourInteractor(Context context) { }

    @Override public Observable<Integer> process() {
        return Observable.just((int)QorumSharedCache.checkSettingsStartTour().get(BaseCacheType.INT))
                .doOnNext(integer -> {
                    if (integer < START_TOUR_SCREENS_NUMBER - 1)
                        throw new StartTourShowingError();
                }).doOnError(err -> {
                    if (err instanceof StartTourShowingError)
                        QorumSharedCache.checkAutoCheckInSettings().save(BaseCacheType.BOOLEAN, true);
                });
    }
}
