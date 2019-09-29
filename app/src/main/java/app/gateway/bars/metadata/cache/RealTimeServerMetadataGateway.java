package app.gateway.bars.metadata.cache;

import app.core.bars.metadata.entity.ServerMetadataModel;
import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface RealTimeServerMetadataGateway {
    Observable<EmptyResponse> clear();
    Observable<ServerMetadataModel> get();
    Observable<ServerMetadataModel> put(ServerMetadataModel model);
}
