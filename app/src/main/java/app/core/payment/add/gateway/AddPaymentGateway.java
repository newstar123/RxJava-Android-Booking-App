package app.core.payment.add.gateway;


import app.core.payment.add.entity.AddPaymentTokenModel;
import app.core.payment.get.entity.GetPaymentCardModel;
import rx.Observable;

public interface AddPaymentGateway {
    Observable<GetPaymentCardModel> add(AddPaymentTokenModel model);
}
