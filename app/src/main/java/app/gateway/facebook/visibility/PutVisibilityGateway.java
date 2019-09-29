package app.gateway.facebook.visibility;

import app.core.facebook.visibility.entity.FacebookVisibilityModel;
import app.core.facebook.visibility.gateway.PutRestFacebookVisibilityGateway;
import app.core.init.token.entity.Token;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutVisibilityGateway implements PutRestFacebookVisibilityGateway {
    private final AndroidAuthTokenGateway androidAuthTokenGateway;
    private PutRetrofitFacebookVisibilityGateway gateway;

    public PutVisibilityGateway(BaseActivity activity) {
        gateway = QorumHttpClient.get().create(PutRetrofitFacebookVisibilityGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<FacebookVisibilityModel> put(FacebookVisibilityModel model) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> update(token, model))
                .compose(Rx401Policy.apply());
    }

    private Observable<FacebookVisibilityModel> update(Token token, FacebookVisibilityModel model) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> gateway.put(tokenWithPrefix, userId, model));
    }
}
