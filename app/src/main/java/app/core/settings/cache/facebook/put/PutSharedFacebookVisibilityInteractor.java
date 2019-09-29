package app.core.settings.cache.facebook.put;

import app.core.BaseInteractor;
import app.core.facebook.visibility.entity.FacebookVisibilityModel;
import app.core.facebook.visibility.interactor.PutRestFacebookVisibilityInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.settings.facebook.model.FacebookVisibilityResponse;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class PutSharedFacebookVisibilityInteractor implements BaseInteractor<FacebookVisibilityResponse, Observable<FacebookVisibilityResponse>> {
    private CheckNetworkPermissionGateway networkPermissionGateway;
    private PutRestFacebookVisibilityInteractor restFacebookVisibilityInteractor;

    public PutSharedFacebookVisibilityInteractor(BaseActivity activity) {
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        restFacebookVisibilityInteractor = new PutRestFacebookVisibilityInteractor(activity);
    }

    @Override public Observable<FacebookVisibilityResponse> process(FacebookVisibilityResponse value) {
        return networkPermissionGateway.check()
                .concatMap(isOk -> restFacebookVisibilityInteractor.process(value.getVisibilityModel()))
                .concatMap(model -> getNewVisibility(value, model))
                .concatMap(this::saveVisibilityState);
    }

    private Observable<FacebookVisibilityResponse> saveVisibilityState(FacebookVisibilityResponse response) {
        return Observable.just(QorumSharedCache.checkSettingsFB().save(BaseCacheType.BOOLEAN, !response.isCheckedState()))
                .concatMap(isChecked -> Observable.just(response));
    }

    private Observable<FacebookVisibilityResponse> getNewVisibility(FacebookVisibilityResponse response, FacebookVisibilityModel model) {
        return Observable.create(subscriber -> {
            response.setVisibilityModel(model);
            subscriber.onNext(response);
            subscriber.onCompleted();
        });
    }
}
