package app.core.checkin.tip.interactor;

import app.core.BaseInteractor;
import app.core.checkin.item.interactor.GetCheckInByIdInteractor;
import app.core.checkin.item.interactor.GetCheckInInteractor;
import app.core.checkin.tip.entity.ConfirmTipsRequest;
import app.core.checkin.user.post.entity.CheckInResponse;
import app.delivering.component.BaseActivity;
import app.gateway.checkin.tip.PutConfirmTipsRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import app.qamode.log.LogToFileHandler;
import rx.Observable;

public class ConfirmTipsInteractor implements BaseInteractor<ConfirmTipsRequest,Observable<CheckInResponse>> {
    private final PutConfirmTipsRestGateway putConfirmTipsRestGateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;
    private final GetCheckInInteractor getCheckInByIdRestGateway;

    public ConfirmTipsInteractor (BaseActivity activity){
        putConfirmTipsRestGateway = new PutConfirmTipsRestGateway(activity);
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
        getCheckInByIdRestGateway = new GetCheckInByIdInteractor(activity);
    }

    @Override public Observable<CheckInResponse> process(ConfirmTipsRequest request) {
        LogToFileHandler.addLog("GetCheckInByIdRestGateway - call from-ConfirmTipsInteractor");
        return networkPermissionGateway.check()
                .map(isOk -> QorumSharedCache.checkSharedCheckInId().get(BaseCacheType.LONG))
                .map(id -> {request.setCheckInID((long)id);
                return request;})
                .concatMap(putConfirmTipsRestGateway::put)
                .concatMap(checkInResponse -> getCheckInByIdRestGateway.process(request.getCheckInID()));
    }

}
