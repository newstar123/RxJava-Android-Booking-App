package app.delivering.mvp.payment.add.edit.number.binder;


import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.trello.rxlifecycle.android.ActivityEvent;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.payment.add.edit.number.binder.handler.AddCardSpansHandler;
import app.delivering.mvp.payment.add.edit.number.binder.handler.ChangeCardTypeHandler;
import app.delivering.mvp.payment.add.edit.number.binder.handler.ChangeInputType;
import app.delivering.mvp.payment.add.edit.number.binder.handler.ValidatorResultHandler;
import app.delivering.mvp.payment.add.edit.number.binder.validator.CardType;
import app.delivering.mvp.payment.add.edit.number.binder.validator.EditTextError;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidation;
import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class OnNumberEditBinder extends BaseBinder {
    private final AddCardSpansHandler addCardSpansHandler;
    private final ValidatorResultHandler validatorResultHandler;
    @BindView(R.id.payment_add_number_input) EditText paymentNumberEditText;
    @BindView(R.id.payment_add_number_layout) TextInputLayout paymentNumberInputLayout;
    @BindView(R.id.payment_add_type_image) ImageView paymentAddTypeImage;
    @BindView(R.id.payment_add_cvv_input) EditText cvvEditText;
    private final ChangeCardTypeHandler changeCardTypeHandler;
    private PublishSubject<Editable> subject;
    private EditTextError numberInputError;

    public OnNumberEditBinder(BaseActivity activity) {
        super(activity);
        changeCardTypeHandler = new ChangeCardTypeHandler();
        addCardSpansHandler = new AddCardSpansHandler();
        validatorResultHandler = new ValidatorResultHandler(AddPaymentValidation.NUMBER);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        subject = PublishSubject.create();
        numberInputError = new EditTextError(paymentNumberInputLayout);
        RxTextView.afterTextChangeEvents(paymentNumberEditText)
                .map(TextViewAfterTextChangeEvent::editable)
                .compose(getActivity().bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(subject);
        subject.asObservable().flatMap(changeCardTypeHandler::transform)
                .subscribe(this::changePaymentInputs, e -> {}, () -> {});
        subject.asObservable().flatMap(addCardSpansHandler::transform)
                .map(this::validateNumberField)
                .subscribe(isValid -> {
                    if (isValid)
                        numberInputError.reset();
                    validatorResultHandler.send(isValid);
                }, e -> {});
        initFocusChange();
        paymentNumberEditText.requestFocus();
    }

    private void initFocusChange() {
        paymentNumberEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Observable.just(paymentNumberEditText.getText())
                        .map(this::validateNumberField)
                        .filter(isValid -> !isValid)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isValid -> {
                            numberInputError.show(R.string.card_number_not_valid);
                            validatorResultHandler.send(isValid);
                        }, e -> {});
            }
        });
    }

    private void changePaymentInputs(CardType cardType) {
        ChangeInputType.changeInputType(cvvEditText, cardType.getSecurityCodeLength());
        ChangeInputType.changeInputType(paymentNumberEditText, cardType.getMaxCardLength());
        if (cardType == CardType.UNKNOWN)
            paymentAddTypeImage.setBackground(null);
        else
            paymentAddTypeImage.setBackgroundResource(cardType.getFrontResource());
    }

    private boolean validateNumberField(Editable editable) {
        String currentValue = editable.toString();
        CardType cardType = CardType.forCardNumber(currentValue);
        return cardType.validate(currentValue);
    }

}
