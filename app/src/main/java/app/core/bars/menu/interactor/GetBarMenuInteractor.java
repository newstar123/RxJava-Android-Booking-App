package app.core.bars.menu.interactor;

import app.core.BaseInteractor;
import app.core.bars.menu.entity.BarMenuRestModel;
import app.delivering.component.BaseActivity;
import app.gateway.bars.menu.GetBarMenuRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetBarMenuInteractor implements BaseInteractor<Long,Observable<BarMenuRestModel>> {
    private GetBarMenuRestGateway menuRestGateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;

    public GetBarMenuInteractor(BaseActivity activity){
        menuRestGateway = new GetBarMenuRestGateway();
        networkPermissionGateway = new CheckNetworkPermissionGateway(activity);
    }

    @Override public Observable<BarMenuRestModel> process(Long id) {
        return networkPermissionGateway.check()
        .concatMap(isOk -> menuRestGateway.get(id));
    }
}
