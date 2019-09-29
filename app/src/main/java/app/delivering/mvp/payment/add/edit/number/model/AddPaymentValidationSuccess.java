package app.delivering.mvp.payment.add.edit.number.model;



public class AddPaymentValidationSuccess {

    private AddPaymentValidation addPaymentValidation;

    public AddPaymentValidationSuccess(AddPaymentValidation addPaymentValidation) {
        this.addPaymentValidation = addPaymentValidation;
    }

    public AddPaymentValidation getAddPaymentValidation() {
        return addPaymentValidation;
    }

    public void setAddPaymentValidation(AddPaymentValidation addPaymentValidation) {
        this.addPaymentValidation = addPaymentValidation;
    }
}
