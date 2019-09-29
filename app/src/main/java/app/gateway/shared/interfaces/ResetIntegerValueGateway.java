package app.gateway.shared.interfaces;

import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface ResetIntegerValueGateway {
    Observable<EmptyResponse> reset();
}