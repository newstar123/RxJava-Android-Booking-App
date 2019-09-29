package app.core.coachmark;

import app.core.BaseOutputInteractor;
import app.core.login.check.foreground.ForegroundCheckAccountInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import rx.schedulers.Schedulers;

public class ProfileCoachMarkInteractor implements BaseOutputInteractor<Observable<Integer>> {
    private final ForegroundCheckAccountInteractor checkAccountInteractor;


    public ProfileCoachMarkInteractor(BaseActivity context) {
        checkAccountInteractor = new ForegroundCheckAccountInteractor(context);
    }

    @Override
    public Observable<Integer> process() {
        return checkAccountInteractor.process()
                .doOnNext(ForegroundCheckAccountInteractor::checkLoggedIn)
                .observeOn(Schedulers.io())
                .concatMap(token -> Observable.just((int)QorumSharedCache.checkReminder().get(BaseCacheType.INT)))
                .flatMap(savedCount -> {
                    Integer finalSavedCount = ++savedCount;
                    return Observable.just(QorumSharedCache.checkReminder().save(BaseCacheType.INT, finalSavedCount))
                                    .map(val -> finalSavedCount);
                });
    }
}
