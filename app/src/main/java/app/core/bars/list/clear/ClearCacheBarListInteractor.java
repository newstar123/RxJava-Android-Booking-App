package app.core.bars.list.clear;

import app.core.BaseOutputInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.gateway.bars.list.cache.clear.ClearBarListRealTimeGateway;
import rx.Observable;

public class ClearCacheBarListInteractor implements BaseOutputInteractor<Observable<EmptyResponse>> {
    private final ClearBarListRealTimeGateway clearBarListRealTimeGateway;

    public ClearCacheBarListInteractor() {
        clearBarListRealTimeGateway = new ClearBarListRealTimeGateway();
    }

    @Override public Observable<EmptyResponse> process() {
        return clearBarListRealTimeGateway.clear();
    }
}
