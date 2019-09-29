package app.delivering.mvp.main.service.init.presenter;

import app.core.gcm.interactor.PutNotificationTokenInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class CheckUpdateGcmTokenPresenter extends BaseOutputPresenter<Observable<EmptyResponse>> {
    private final PutNotificationTokenInteractor putNotificationTokenInteractor;

    public CheckUpdateGcmTokenPresenter(BaseActivity activity) {
        super(activity);
        putNotificationTokenInteractor = new PutNotificationTokenInteractor(activity);
    }

    @Override public Observable<EmptyResponse> process() {
        return Observable.just((Boolean)QorumSharedCache.checkGCMTokenUpdates().get(BaseCacheType.BOOLEAN))
                .filter(isUpdated -> isUpdated)
                .concatMap(isOk -> putNotificationTokenInteractor.process());
    }
}
