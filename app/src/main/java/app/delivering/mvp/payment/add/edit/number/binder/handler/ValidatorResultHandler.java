package app.delivering.mvp.payment.add.edit.number.binder.handler;


import org.greenrobot.eventbus.EventBus;

import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidation;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidationError;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidationSuccess;

public class ValidatorResultHandler {
    private AddPaymentValidation addPaymentValidation;

    public ValidatorResultHandler(AddPaymentValidation addPaymentValidation) {
        this.addPaymentValidation = addPaymentValidation;
    }

    public void send(boolean isValid) {
        if (isValid)
            EventBus.getDefault().post(new AddPaymentValidationSuccess(addPaymentValidation));
        else
            EventBus.getDefault().post(new AddPaymentValidationError(addPaymentValidation));
    }
}
