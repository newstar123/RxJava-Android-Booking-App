package app.core.payment.get.gateway;


import app.core.payment.get.entity.GetPaymentCardsModel;
import rx.Observable;

public interface GetPaymentsGateway {
    Observable<GetPaymentCardsModel> get();
}
