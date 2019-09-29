package app.core.payment.add.gateway;


import app.core.payment.add.entity.AddPaymentModel;
import rx.Observable;

public interface GetPaymentTokenGateway {
    Observable<String> get(AddPaymentModel addPaymentModel);
}
