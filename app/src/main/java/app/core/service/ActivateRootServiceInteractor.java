package app.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.checkin.cacheid.GetSharedCheckInIdInteractor;
import app.core.checkin.list.GetListCheckInsInteractor;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.main.service.init.model.ActivateServicesResult;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class ActivateRootServiceInteractor implements BaseOutputInteractor<Observable<ActivateServicesResult>> {
    private CheckNetworkPermissionGateway checkNetworkPermissionGateway;
    private GetListCheckInsInteractor getListCheckInsInteractor;
    private GetSharedCheckInIdInteractor getSharedCheckInIdInteractor;

    public ActivateRootServiceInteractor(BaseActivity activity) {
        checkNetworkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        getListCheckInsInteractor = new GetListCheckInsInteractor(activity);
        getSharedCheckInIdInteractor = new GetSharedCheckInIdInteractor();
    }

    @Override public Observable<ActivateServicesResult> process() {
        return checkNetworkPermissionGateway.check()
                .concatMap(isOk -> checkResponses())
                .concatMap(this::checkCheckOut);
    }

    private Observable<ActivateServicesResult> checkResponses() {
        return Observable.zip(getListCheckInsInteractor.process(),
                              getSharedCheckInIdInteractor.process(),
                              Observable.just((long)QorumSharedCache.checkCheckoutId().get(BaseCacheType.LONG)),
                              (checkInsResponse, checkInId, checkOutId) -> {
                                  ActivateServicesResult result = new ActivateServicesResult();
                                  ArrayList<GetCheckInsResponse> listActiveCheckIns = new ArrayList<>();
                                  GetCheckInsResponse activeCheckIn = null;
                                  GetCheckInsResponse lastSavedCheckIn = null;
                                  GetCheckInsResponse savedCheckOut = null;
                                  for (GetCheckInsResponse response : checkInsResponse) {
                                      if (response.getCheckoutTime() == null)
                                          listActiveCheckIns.add(response);
                                      if (response.getId() == checkInId)
                                          lastSavedCheckIn = response;
                                      if (response.getId() == checkOutId)
                                          savedCheckOut = response;
                                  }
                                  activeCheckIn = getLastCheckIn(listActiveCheckIns);
                                  result.setLatestCheckInSaved(checkActiveCheckInSaved(activeCheckIn, lastSavedCheckIn));
                                  result.setCheckOut(savedCheckOut);
                                  result.setActiveCheckIn(activeCheckIn);
                                  result.setLastSavedCheckIn(lastSavedCheckIn);
                                  return result;
                              });
    }

    private GetCheckInsResponse getLastCheckIn(List<GetCheckInsResponse> listActiveCheckIns) {
        if (!listActiveCheckIns.isEmpty()) {
            Collections.sort(listActiveCheckIns, (o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));
            return listActiveCheckIns.get(0);
        } else
            return null;
    }

    private boolean checkActiveCheckInSaved(GetCheckInsResponse activeCheckIn, GetCheckInsResponse lastSavedCheckIn) {
        if (activeCheckIn == null || lastSavedCheckIn == null)
            return false;
        else return activeCheckIn.getId() == lastSavedCheckIn.getId();
    }

    private Observable<ActivateServicesResult> checkCheckOut(ActivateServicesResult activateServicesResult) {
        if (activateServicesResult.isLatestCheckInSaved())
            return Observable.just(activateServicesResult);
        else
            return Observable.just(QorumSharedCache.checkSharedCheckInId().save(BaseCacheType.LONG, 0))
                    .concatMap(emptyCheckInId -> Observable.just(QorumSharedCache.checkBarCacheId().save(BaseCacheType.LONG, 0)))
                    .concatMap(emptyBarId -> Observable.just(activateServicesResult));
    }
}
