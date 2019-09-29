package app.delivering.mvp.payment.add.edit.number.model;



public class AddPaymentValidationError {
    private AddPaymentValidation addPaymentValidation;

    public AddPaymentValidationError(AddPaymentValidation addPaymentValidation) {
        this.addPaymentValidation = addPaymentValidation;
    }

    public AddPaymentValidation getAddPaymentValidation() {
        return addPaymentValidation;
    }

    public void setAddPaymentValidation(AddPaymentValidation addPaymentValidation) {
        this.addPaymentValidation = addPaymentValidation;
    }
}
