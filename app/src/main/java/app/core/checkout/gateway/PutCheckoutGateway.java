package app.core.checkout.gateway;


import app.core.payment.regular.model.EmptyResponse;
import rx.Observable;

public interface PutCheckoutGateway {
    Observable<EmptyResponse> put(long checkInId);
}
