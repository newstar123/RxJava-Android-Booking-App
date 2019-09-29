package app.delivering.mvp.payment.add.edit.cvv;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.payment.add.edit.number.binder.handler.ValidatorResultHandler;
import app.delivering.mvp.payment.add.edit.number.binder.validator.CardType;
import app.delivering.mvp.payment.add.edit.number.binder.validator.EditTextError;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidation;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class OnCVVEditBinder extends BaseBinder {
    private final ValidatorResultHandler validatorResultHandler;
    @BindView(R.id.payment_add_cvv_input) EditText cvvEditText;
    @BindView(R.id.payment_add_cvv_layout) TextInputLayout cvvInputLayout;
    @BindView(R.id.payment_add_number_input) EditText paymentNumberEditText;
    private EditTextError cvvInputError;

    public OnCVVEditBinder(BaseActivity activity) {
        super(activity);
        validatorResultHandler = new ValidatorResultHandler(AddPaymentValidation.CVV);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        cvvInputError = new EditTextError(cvvInputLayout);
        init();
    }

    private void init() {
        trackingValidValue();
        trackingErrorAfterChangeFocus();
    }

    private void trackingValidValue() {
        RxTextView.afterTextChangeEvents(cvvEditText)
                .map(TextViewAfterTextChangeEvent::editable)
                .map(editable -> validateCVV(editable.toString()))
                .subscribe(isValid -> {
                    if (isValid)
                        cvvInputError.reset();
                    validatorResultHandler.send(isValid);
                }, e -> {});
    }

    private void trackingErrorAfterChangeFocus() {
        cvvEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Observable.just(cvvEditText.getText().toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(this::validateCVV)
                        .filter(isValid -> !isValid)
                        .subscribe(isValid -> {
                            cvvInputError.show(R.string.invalid_security_code);
                            validatorResultHandler.send(isValid);
                            cvvInputLayout.clearFocus();
                        }, e -> {});
            }
        });
    }

    private boolean validateCVV(String string) {
        String cardNumber = paymentNumberEditText.getText().toString().replaceAll("\\s+", "");
        CardType cardType = CardType.forCardNumber(cardNumber);
        int securityCodeLength = cardType.getSecurityCodeLength();
        return securityCodeLength == string.length();
    }

}
