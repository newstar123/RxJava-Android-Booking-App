package app.gateway.bars.metadata.cache;

import app.core.bars.metadata.entity.ServerMetadataModel;
import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public class RealTimeCacheServerMetadataGateway implements RealTimeServerMetadataGateway{

    @Override
    public Observable<EmptyResponse> clear() {
        return Observable.just(new EmptyResponse())
                .doOnNext(emptyResponse -> ServerMetadataRealTimeHolder.clear());
    }

    @Override
    public Observable<ServerMetadataModel> get() {
        return ServerMetadataRealTimeHolder.getMetadata();
    }

    @Override
    public Observable<ServerMetadataModel> put(ServerMetadataModel model) {
        return ServerMetadataRealTimeHolder.setList(model);
    }
}
