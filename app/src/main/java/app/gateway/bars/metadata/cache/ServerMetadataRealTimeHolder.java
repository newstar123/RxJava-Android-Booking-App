package app.gateway.bars.metadata.cache;

import app.core.bars.metadata.entity.ServerMetadataModel;
import rx.Observable;

public class ServerMetadataRealTimeHolder {
    private static ServerMetadataModel model;
    private static final Object modelLock = new Object();

    public static Observable<ServerMetadataModel> setList(ServerMetadataModel model){
        synchronized (modelLock){
            ServerMetadataRealTimeHolder.model = model;
           return getMetadata();
        }
    }

    public static Observable<ServerMetadataModel> getMetadata(){
        synchronized (modelLock){
            return Observable.just(ServerMetadataRealTimeHolder.model);
        }
    }

    public static void clear(){
        synchronized (modelLock){
            model = null;
        }
    }
}
