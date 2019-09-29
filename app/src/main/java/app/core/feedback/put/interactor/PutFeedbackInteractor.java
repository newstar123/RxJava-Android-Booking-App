package app.core.feedback.put.interactor;

import app.core.BaseInteractor;
import app.core.feedback.put.entity.FeedbackRequestModel;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.gateway.feedback.PutFeedbackGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class PutFeedbackInteractor implements BaseInteractor<FeedbackRequestModel,Observable<EmptyResponse>> {
    private PutFeedbackGateway putFeedbackGateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;

   public PutFeedbackInteractor(BaseActivity activity){
       putFeedbackGateway = new PutFeedbackGateway(activity);
       networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
   }

    @Override public Observable<EmptyResponse> process(FeedbackRequestModel requestModel) {
        return networkPermissionGateway.check()
                .concatMap(isOk -> putFeedbackGateway.put(requestModel))
                .concatMap(this::clearCheckOut);
    }

    private Observable<EmptyResponse> clearCheckOut(EmptyResponse response) {
        return Observable.just(QorumSharedCache.checkCheckoutId().save(BaseCacheType.LONG, 0))
                .map(emptyId -> response);
    }
}
