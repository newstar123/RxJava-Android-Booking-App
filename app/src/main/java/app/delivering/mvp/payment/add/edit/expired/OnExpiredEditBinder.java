package app.delivering.mvp.payment.add.edit.expired;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.payment.add.edit.number.binder.handler.ValidatorResultHandler;
import app.delivering.mvp.payment.add.edit.number.binder.validator.EditTextError;
import app.delivering.mvp.payment.add.edit.number.binder.validator.RxBindingValidator;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidation;
import butterknife.BindView;
import butterknife.OnClick;


public class OnExpiredEditBinder extends BaseBinder {
    private final ValidatorResultHandler validatorResultHandler;
    @BindView(R.id.payment_add_expired_date_layout) TextInputLayout expiredInputLayout;
    @BindView(R.id.payment_add_expired_date_input) EditText expiredEditText;
    private EditTextError expiredInputError;
    private Calendar expired;
    private DatePickerDialog dialogToUpdateExpired;

    public OnExpiredEditBinder(BaseActivity activity) {
        super(activity);
        validatorResultHandler = new ValidatorResultHandler(AddPaymentValidation.EXPIRED);
        expired = Calendar.getInstance();
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        expiredInputError = new EditTextError(expiredInputLayout);
        RxBindingValidator.create(expiredEditText, getActivity())
                .map(this::validate)
                .subscribe(validatorResultHandler::send, e -> {});
    }

    private boolean validate(String s) {
        if (s.length() < 1)
            return expiredInputError.show(R.string.invalid_expired_date);
        return !expiredInputLayout.isErrorEnabled();
    }

    @OnClick(R.id.payment_add_expired_date_input) void expiredDate() {
        if (dialogToUpdateExpired != null) {
            dialogToUpdateExpired.show();
            return;
        }

        dialogToUpdateExpired = new DatePickerDialog(getActivity(), R.style.MonthYearDialog,
                (view, year, month, dayOfMonth) -> {
                    expired.set(Calendar.YEAR, year);
                    expired.set(Calendar.MONTH, month);
                    expired.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    applyExpired();
                },
                expired.get(Calendar.YEAR), expired.get(Calendar.MONTH), expired.get(Calendar.DAY_OF_MONTH));

        DatePicker datePicker = dialogToUpdateExpired.getDatePicker();
        View dayView = datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android"));

        if (dayView != null) {
            dayView.setVisibility(View.GONE);
        }

        Calendar minExpired = Calendar.getInstance();
        minExpired.set(Calendar.DAY_OF_MONTH, minExpired.getActualMaximum(Calendar.DAY_OF_MONTH));
        datePicker.setMinDate(minExpired.getTimeInMillis());

        Calendar maxExpired = Calendar.getInstance();
        maxExpired.setTimeInMillis(minExpired.getTimeInMillis());
        maxExpired.add(Calendar.YEAR, 10);
        maxExpired.add(Calendar.MONTH, -1);
        datePicker.setMaxDate(maxExpired.getTimeInMillis());

        dialogToUpdateExpired.show();
    }

    private void applyExpired() {
        expiredInputError.reset();

        SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.getDefault());
        expiredEditText.setText(formatter.format(expired.getTime()));
    }
}
