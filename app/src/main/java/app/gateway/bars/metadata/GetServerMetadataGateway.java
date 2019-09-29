package app.gateway.bars.metadata;

import app.core.bars.metadata.GetServerMetadataInterface;
import app.core.bars.metadata.entity.ServerMetadataModel;
import app.gateway.bars.metadata.cache.RealTimeCacheServerMetadataGateway;
import app.gateway.bars.metadata.cache.RealTimeServerMetadataGateway;
import app.gateway.bars.metadata.exception.EmptyRealtimeMetadataException;
import app.gateway.rest.client.QorumHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class GetServerMetadataGateway implements GetServerMetadataInterface {
    private final GetServerMetadataRestGateway restGateway;
    private final RealTimeServerMetadataGateway realTimeCacheGateway;

    public GetServerMetadataGateway() {
        restGateway = QorumHttpClient.get().create(GetServerMetadataRestGateway.class);
        realTimeCacheGateway = new RealTimeCacheServerMetadataGateway();
    }

    @Override public Observable<ServerMetadataModel> get() {
        return realTimeCacheGateway.get()
                .subscribeOn(Schedulers.io())
                .doOnNext(metadata -> {
                    if (metadata == null)
                        throw new EmptyRealtimeMetadataException();
                })
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof EmptyRealtimeMetadataException) {
                        return getRestMetadataAndSaveToRealtime();
                    } else
                        return Observable.error(throwable.getCause());
                });
    }

    private Observable<ServerMetadataModel> getRestMetadataAndSaveToRealtime() {
        return restGateway.get().concatMap(realTimeCacheGateway::put);
    }

}
