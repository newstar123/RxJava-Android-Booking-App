package app.gateway.bars.list.cache.clear;

import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface ClearBarListInRealTimeGateway {
    Observable<EmptyResponse> clear();
}
