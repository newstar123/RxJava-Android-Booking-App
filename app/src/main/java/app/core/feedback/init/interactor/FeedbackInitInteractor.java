package app.core.feedback.init.interactor;

import app.core.BaseInteractor;
import app.core.bars.detail.gateway.GetBarItemGateway;
import app.core.feedback.init.entity.FeedbackOutputModel;
import app.core.location.image.interactor.LocationImageInteractor;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.bars.item.GetBarItemRestGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FeedbackInitInteractor implements BaseInteractor<Long, Observable<FeedbackOutputModel>> {
    private final PermissionInteractor permissionInteractor;
    private final LocationImageInteractor locationImageInteractor;
    private final GetBarItemGateway barItemGateway;


    public FeedbackInitInteractor(BaseActivity activity) {
        permissionInteractor = new PermissionInteractor(activity);
        locationImageInteractor = new LocationImageInteractor(activity);
        barItemGateway = new GetBarItemRestGateway();
    }

    @Override public Observable<FeedbackOutputModel> process(Long barId) {
        return permissionInteractor.process().observeOn(Schedulers.io()).concatMap(isGranted -> loadInParallel(barId));
    }

    private Observable<FeedbackOutputModel> loadInParallel(long barId) {
        return Observable.merge(createLocationImageMerged(), createBarDetailMerged(barId));
    }

    private Observable<FeedbackOutputModel> createLocationImageMerged() {
        return locationImageInteractor.process().map(FeedbackOutputModel::new);
    }

    private Observable<FeedbackOutputModel> createBarDetailMerged(long barId) {
        return barItemGateway.get(barId).map(FeedbackOutputModel::new);
    }
}
