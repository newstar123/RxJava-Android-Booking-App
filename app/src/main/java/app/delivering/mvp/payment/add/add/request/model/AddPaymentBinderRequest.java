package app.delivering.mvp.payment.add.add.request.model;


import app.core.payment.add.entity.AddPaymentModel;

public class AddPaymentBinderRequest {
    private AddPaymentModel addPaymentModel;
    private String expiredMonthYear;

    public AddPaymentModel getAddPaymentModel() {
        return addPaymentModel;
    }

    public void setAddPaymentModel(AddPaymentModel addPaymentModel) {
        this.addPaymentModel = addPaymentModel;
    }

    public String getExpiredMonthYear() {
        return expiredMonthYear;
    }

    public void setExpiredMonthYear(String expiredMonthYear) {
        this.expiredMonthYear = expiredMonthYear;
    }
}
