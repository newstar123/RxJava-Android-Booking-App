package app.core.settings.cache.common.interactor;

import app.core.BaseOutputInteractor;
import app.core.settings.cache.common.entity.SettingsModel;
import app.delivering.component.BaseActivity;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetSharedSettingsInteractor implements BaseOutputInteractor<Observable<SettingsModel>> {

    public GetSharedSettingsInteractor(BaseActivity activity){
    }

    @Override public Observable<SettingsModel> process() {
        return Observable.zip(Observable.just(QorumSharedCache.checkSettingsFB().get(BaseCacheType.BOOLEAN)), getAutoCheckInSettings(),
                              this::getSettingsModel);
    }

    private Observable<Boolean> getAutoCheckInSettings() {
        return Observable.just(true)
                .map(val -> QorumSharedCache.checkAutoCheckInSettings().get(BaseCacheType.BOOLEAN));
    }

    private SettingsModel getSettingsModel(Boolean isFacebookVisible, Boolean isAutoOpen) {
        SettingsModel settingsModel = new SettingsModel();
        settingsModel.setFacebookVisibility(isFacebookVisible);
        settingsModel.setAutoOpenTab(isAutoOpen);
        return settingsModel;
    }
}
