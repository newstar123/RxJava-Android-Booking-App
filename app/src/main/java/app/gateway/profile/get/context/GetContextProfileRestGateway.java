package app.gateway.profile.get.context;

import android.content.Context;

import app.core.init.token.entity.Token;
import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.gateway.GetProfileGateway;
import app.gateway.auth.context.AuthTokenWithContextGateway;
import app.gateway.profile.get.GetProfileRetrofitGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetContextProfileRestGateway implements GetProfileGateway {
    private final GetProfileRetrofitGateway profileGateway;
    private final AuthTokenWithContextGateway androidAuthTokenGateway;

    public GetContextProfileRestGateway(Context context) {
        profileGateway = QorumHttpClient.get().create(GetProfileRetrofitGateway.class);
        androidAuthTokenGateway = new AuthTokenWithContextGateway(context);
    }

    @Override public Observable<ProfileModel> get() {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(this::getProfile)
                .compose(Rx401Policy.apply());
    }

    private Observable<ProfileModel> getProfile(Token token) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId ->profileGateway.get(tokenWithPrefix, userId));
    }

}
