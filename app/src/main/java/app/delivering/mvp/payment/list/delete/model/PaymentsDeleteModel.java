package app.delivering.mvp.payment.list.delete.model;


import app.delivering.mvp.payment.list.init.model.PaymentsInitBinderModel;

public class PaymentsDeleteModel {
    private PaymentsInitBinderModel paymentsInitBinderModel;
    private int position;

    public PaymentsInitBinderModel getPaymentsInitBinderModel() {
        return paymentsInitBinderModel;
    }

    public void setPaymentsInitBinderModel(PaymentsInitBinderModel paymentsInitBinderModel) {
        this.paymentsInitBinderModel = paymentsInitBinderModel;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
