package app.core.payment.delete.gateway;


import rx.Observable;

public interface DeletePaymentGateway {
    Observable<String> delete(String token);
}
