package app.delivering.mvp.profile.drawer.open.presenter;

import app.core.profile.get.interactor.GetProfileSafeGuestModeInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.profile.drawer.model.NavigationDrawerInitModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class NavigationDrawerProfilePresenter extends BaseOutputPresenter<Observable<NavigationDrawerInitModel>> {
    private final GetProfileSafeGuestModeInteractor safeGuestModeInteractor;

    public NavigationDrawerProfilePresenter(BaseActivity activity) {
        super(activity);
        safeGuestModeInteractor = new GetProfileSafeGuestModeInteractor(activity);
    }

    @Override
    public Observable<NavigationDrawerInitModel> process() {
        return Observable.zip(safeGuestModeInteractor.process(), Observable.just(QorumSharedCache.checkLocationImage().get(BaseCacheType.STRING)),
                NavigationDrawerInitModel::new);
    }
}
