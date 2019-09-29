package app.delivering.mvp.payment.list.init.events;

import app.delivering.mvp.payment.list.init.model.PaymentsInitBinderModel;

public class PaymentsInitBinderModelEvent {

    private PaymentsInitBinderModel paymentsInitBinderModel;

    public PaymentsInitBinderModelEvent(PaymentsInitBinderModel paymentsInitBinderModel) {
        this.paymentsInitBinderModel = paymentsInitBinderModel;
    }

    public PaymentsInitBinderModel getPaymentsInitBinderModel() {
        return paymentsInitBinderModel;
    }
}
