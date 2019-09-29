package app.gateway.feedback;

import app.core.feedback.put.entity.FeedbackRequestModel;
import app.core.feedback.put.gateway.FeedbackGateway;
import app.core.init.token.entity.Token;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.gateway.rest.client.QorumHttpClient;
import app.gateway.rest.client.Rx401Policy;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutFeedbackGateway implements FeedbackGateway {
    private final AndroidAuthTokenGateway androidAuthTokenGateway;
    private FeedbackRetrofitGateway gateway;

    public PutFeedbackGateway(BaseActivity activity) {
        gateway = QorumHttpClient.get().create(FeedbackRetrofitGateway.class);
        androidAuthTokenGateway = new AndroidAuthTokenGateway(activity);
    }

    @Override public Observable<EmptyResponse> put(FeedbackRequestModel requestModel) {
        return androidAuthTokenGateway.get()
                .observeOn(Schedulers.io())
                .concatMap(token -> feedback(token, requestModel))
                .compose(Rx401Policy.apply());
    }

    private Observable<EmptyResponse> feedback(Token token, FeedbackRequestModel requestModel) {
        String tokenWithPrefix = QorumHttpClient.createTokenWithPrefix(token);
        return Observable.just((long)QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(userId -> gateway.put(tokenWithPrefix, userId, requestModel.getCheckInId(), requestModel.getBody()));
    }
}
