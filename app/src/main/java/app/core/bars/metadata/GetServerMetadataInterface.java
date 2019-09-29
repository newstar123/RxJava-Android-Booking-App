package app.core.bars.metadata;

import app.core.bars.metadata.entity.ServerMetadataModel;
import rx.Observable;

public interface GetServerMetadataInterface {
    Observable<ServerMetadataModel> get();
}
