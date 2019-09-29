package app.gateway.bars.locations.cache.clear;

import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface ClearLocationListInRealTimeGateway {
    Observable<EmptyResponse> clear();
}
