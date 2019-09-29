package app.core.bars.detail.interactor;

import android.content.Context;

import app.core.BaseInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.gateway.bars.item.GetBarItemRestGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetItemBarInteractor implements BaseInteractor<Long,Observable<BarModel>> {
    private GetBarItemRestGateway itemRestGateway;
    private CheckNetworkPermissionGateway networkPermissionGateway;

    public GetItemBarInteractor(Context context){
        itemRestGateway = new GetBarItemRestGateway();
        networkPermissionGateway = new CheckNetworkPermissionGateway(context);
    }

    @Override public Observable<BarModel> process(Long id) {
        return networkPermissionGateway.check()
                .concatMap(isOk -> itemRestGateway.get(id));
    }
}
