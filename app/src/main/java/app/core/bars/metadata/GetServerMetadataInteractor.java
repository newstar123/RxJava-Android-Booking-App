package app.core.bars.metadata;

import android.content.Context;

import app.core.BaseOutputInteractor;
import app.core.bars.metadata.entity.ServerMetadataModel;
import app.gateway.bars.metadata.GetServerMetadataGateway;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import rx.Observable;

public class GetServerMetadataInteractor implements BaseOutputInteractor<Observable<ServerMetadataModel>> {
    private final GetServerMetadataInterface getServerMetadata;
    private final CheckNetworkPermissionGateway networkPermission;

    public GetServerMetadataInteractor(Context context) {
        getServerMetadata = new GetServerMetadataGateway();
        networkPermission = new CheckNetworkPermissionGateway(context);
    }

    @Override
    public Observable<ServerMetadataModel> process() {
        return networkPermission.check()
                .concatMap(isOk -> getServerMetadata.get());
    }
}
