package app.gateway.profile.cache.clear;

import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface ClearProfileInRealTimeGateway {
    Observable<EmptyResponse> clear();
}
