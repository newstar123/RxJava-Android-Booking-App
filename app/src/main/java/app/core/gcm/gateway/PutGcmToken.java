package app.core.gcm.gateway;

import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface PutGcmToken {
    Observable<EmptyResponse> put(String token);
}
