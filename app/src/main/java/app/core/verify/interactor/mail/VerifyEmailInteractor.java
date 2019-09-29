package app.core.verify.interactor.mail;

import app.core.BaseInteractor;
import app.core.login.facebook.entity.LoginResponse;
import app.core.profile.put.entity.PutProfileModel;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.profile.put.PutProfileRestGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import rx.schedulers.Schedulers;

public class VerifyEmailInteractor implements BaseInteractor<PutProfileModel, Observable<LoginResponse>> {
    private final CheckNetworkPermissionGateway networkPermissionGateway;
    private final PutProfileRestGateway putProfileRestGateway;


    public VerifyEmailInteractor(BaseActivity baseActivity) {
        networkPermissionGateway = new CheckNetworkPermissionGateway(baseActivity);
        putProfileRestGateway = new PutProfileRestGateway(baseActivity);
    }

    @Override
    public Observable<LoginResponse> process(PutProfileModel putProfileModel) {
        return networkPermissionGateway.check()
                .map(isOk -> (long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> putProfileRestGateway.put(userId, putProfileModel))
                .observeOn(Schedulers.io());
    }
}
