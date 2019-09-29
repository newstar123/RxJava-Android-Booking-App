package app.delivering.mvp.payment.add.edit.zip;


import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.payment.add.edit.number.binder.handler.ValidatorResultHandler;
import app.delivering.mvp.payment.add.edit.number.binder.validator.EditTextError;
import app.delivering.mvp.payment.add.edit.number.binder.validator.RxBindingValidator;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidation;
import butterknife.BindView;

public class OnZipEditBinder extends BaseBinder {
    private final ValidatorResultHandler validatorResultHandler;
    @BindView(R.id.payment_add_zip_input) EditText zipEditText;
    @BindView(R.id.payment_add_zip_layout) TextInputLayout zipInputLayout;
    private EditTextError zipInputError;

    public OnZipEditBinder(BaseActivity activity) {
        super(activity);
        validatorResultHandler = new ValidatorResultHandler(AddPaymentValidation.ZIP);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        zipInputError = new EditTextError(zipInputLayout);
        RxBindingValidator.create(zipEditText, getActivity())
                .map(this::validateZip)
                .subscribe(validatorResultHandler::send, e -> {});
    }

    private boolean validateZip(String s) {
        if (s.length() > 0)
            return zipInputError.reset();
        else
            return zipInputError.show(R.string.invalid_zip_code);
    }
}
