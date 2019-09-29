package app.core.payment.regular.gateway;


import rx.Observable;

public interface PutRegularPaymentGateway {
    Observable<String> put(String cardToken);
}
