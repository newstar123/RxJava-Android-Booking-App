package app.gateway.bars.metadata;

import app.core.bars.metadata.entity.ServerMetadataModel;
import retrofit2.http.GET;
import rx.Observable;

public interface GetServerMetadataRestGateway {
    @GET("v2/metadata") Observable<ServerMetadataModel> get();
}
