package app.core.init.token.interactor;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.core.bars.list.get.interactor.GetBarListInteractor;
import app.core.init.branch.InitBranchInteractor;
import app.core.location.image.interactor.LocationImageInteractor;
import app.core.permission.entity.GetAccountPermissionException;
import app.core.starttour.GetStartTourInteractor;
import app.core.version.CheckAppUpdatesInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.main.location.reason.model.InitialLocationReasonError;
import app.gateway.bars.list.cache.holder.BarListRealTimeHolder;
import app.gateway.location.settings.GoogleLocationSettingsGateway;
import app.gateway.permissions.account.CheckAccountPermissionGateway;
import app.gateway.permissions.location.CheckGPSPermissionGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class InitAppInteractor implements BaseOutputInteractor<Observable<String>> {
    private final CheckAppUpdatesInteractor checkAppUpdatesInteractor;
    private final InitBranchInteractor branchInteractor;
    private final CheckNetworkPermissionGateway networkPermissionGateway;
    private final GetStartTourInteractor getStartTourInteractor;
    private final CheckAccountPermissionGateway accountPermissionGateway;
    private final CheckGPSPermissionGateway gpsPermissionGateway;
    private final GoogleLocationSettingsGateway locationSettingsGateway;
    private final LocationImageInteractor locationImageInteractor;
    private GetBarListInteractor getBarListInteractor;

    public InitAppInteractor(BaseActivity activity) {
        checkAppUpdatesInteractor = new CheckAppUpdatesInteractor();
        branchInteractor = new InitBranchInteractor(activity);
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        getStartTourInteractor = new GetStartTourInteractor(activity);
        accountPermissionGateway = new CheckAccountPermissionGateway(activity);
        gpsPermissionGateway = new CheckGPSPermissionGateway(activity);
        locationSettingsGateway = new GoogleLocationSettingsGateway(activity);
        locationImageInteractor = new LocationImageInteractor(activity);
        getBarListInteractor = new GetBarListInteractor(activity);
    }

    @Override public Observable<String> process() {
        return checkAppUpdatesInteractor.process()  //is the latest App version
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(version -> branchInteractor.process()) //Add Branch init part(mail confirmation, venue id)
                .concatMap(version -> networkPermissionGateway.check()) //Check the Internet connection
                .concatMap(isOk -> Observable.just(QorumSharedCache.checkUserAge().get(BaseCacheType.BOOLEAN))) //Check Age screen showed
                .concatMap(isOk -> getStartTourInteractor.process()) //the app onboarding screens
                .concatMap(isChecked -> Observable.just((boolean)QorumSharedCache.checkSharedAccount().get(BaseCacheType.BOOLEAN))) //coach mark “The application needs account and contacts access ...”.
                .doOnNext(isApplied -> {
                    if (!isApplied)
                        throw new GetAccountPermissionException();
                })
                .concatMap(isOk -> accountPermissionGateway.check()) //Check access the accounts creation
                .concatMap(isChecked -> Observable.just((boolean)QorumSharedCache.checkLocationReason().get(BaseCacheType.BOOLEAN))) //coach mark “Qorum uses your current location ...”.
                .doOnNext(isApplied -> {
                    if (!isApplied)
                        throw new InitialLocationReasonError();
                })
                .concatMap(isGranted -> gpsPermissionGateway.check()) //Checks access the GPS
                .concatMap(isGranted -> locationSettingsGateway.get()) //Checks GPS - on
                .concatMap(isOn -> prepareMarketAndVendorLists());
    }

    private Observable<String> prepareMarketAndVendorLists() {
        return Observable.zip(locationImageInteractor.process(), getBarList(),
                (nearestCityImageUrl, savedVendorList) -> nearestCityImageUrl);
    }

    private Observable<List<BarModel>> getBarList() {
        return BarListRealTimeHolder.getList()
                .concatMap(list -> list == null ? getBarListInteractor.process() : Observable.just(list));
    }
}
