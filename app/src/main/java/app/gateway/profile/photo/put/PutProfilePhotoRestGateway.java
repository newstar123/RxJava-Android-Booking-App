package app.gateway.profile.photo.put;

import app.core.init.token.entity.Token;
import app.core.payment.regular.model.EmptyResponse;
import app.core.profile.photo.post.entity.PutProfilePhotoModel;
import app.core.profile.photo.post.gateway.PutProfilePhotoGateway;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutProfilePhotoRestGateway implements PutProfilePhotoGateway {
    private final PutProfilePhotoRetrofitGateway gateway;
    private final AndroidAuthTokenGateway androidAuthTokenGateway;

    public PutProfilePhotoRestGateway(BaseActivity activity) {
        gateway = QorumHttpClient.get().create(PutProfilePhotoRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<EmptyResponse> put(PutProfilePhotoModel model) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> putPhotoFile(token, model))
                .compose(Rx401Policy.apply());
    }

    private Observable<EmptyResponse> putPhotoFile(Token token, PutProfilePhotoModel model) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> getFullBodyRequest(tokenWithPrefix, userId, model));
    }

    private Observable<EmptyResponse> getFullBodyRequest(String tokenWithPrefix, Long userId, PutProfilePhotoModel model) {
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("profile_pic",
                                                                        model.getFile().getName(),
                                                                        RequestBody.create(MediaType.parse("image/jpeg"),
                                                                                           model.getFile()));
        return gateway.put(tokenWithPrefix, userId, filePart);
    }
}
