package app.delivering.mvp.settings.init.presenter;

import app.core.settings.cache.common.entity.SettingsModel;
import app.core.settings.cache.common.interactor.GetSharedSettingsInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import rx.schedulers.Schedulers;

public class InitSettingsStatePresenter extends BaseOutputPresenter<Observable<SettingsModel>> implements SaveToCacheStateInterface {
    private GetSharedSettingsInteractor settingsInteractor;

    public InitSettingsStatePresenter(BaseActivity activity) {
        super(activity);
        settingsInteractor = new GetSharedSettingsInteractor(getActivity());
    }

    @Override public Observable<SettingsModel> process() {
        return settingsInteractor.process();
    }

    @Override
    public Observable<Boolean> save(boolean value) {
        return Observable.just(value)
                .observeOn(Schedulers.io())
                .map(val -> QorumSharedCache.checkAutoCheckInSettings().save(BaseCacheType.BOOLEAN, value));
    }
}
