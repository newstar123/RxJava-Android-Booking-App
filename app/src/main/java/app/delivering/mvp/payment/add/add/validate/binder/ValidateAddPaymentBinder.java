package app.delivering.mvp.payment.add.add.validate.binder;


import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.payment.add.add.request.binder.setter.ResetTextKeepStateSetter;
import app.delivering.mvp.payment.add.add.request.events.AddPaymentEvent;
import app.delivering.mvp.payment.add.add.validate.events.ValidateAddPaymentEvent;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidation;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidationError;
import app.delivering.mvp.payment.add.edit.number.model.AddPaymentValidationSuccess;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class ValidateAddPaymentBinder extends BaseBinder{
    @BindViews({R.id.payment_add_zip_input,
            R.id.payment_add_cvv_input,
            R.id.payment_add_number_input,
            R.id.payment_add_expired_date_input}) List<EditText> editFields;
    private Set<AddPaymentValidation> checkResult;

    public ValidateAddPaymentBinder(BaseActivity activity) {
        super(activity);
        checkResult = new HashSet<>();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentEvent(ValidateAddPaymentEvent event) {
        recheckFields();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentSuccess(AddPaymentValidationSuccess event) {
        checkResult.add(event.getAddPaymentValidation());
        if (checkResult.size() == 5)
            startAddingPayment();
    }

    private void startAddingPayment() {
        checkResult.remove(AddPaymentValidation.REACHECK);
        EventBus.getDefault().post(new AddPaymentEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentError(AddPaymentValidationError event) {
        checkResult.remove(event.getAddPaymentValidation());
        checkResult.remove(AddPaymentValidation.REACHECK);
    }

    private void recheckFields() {
        checkResult.add(AddPaymentValidation.REACHECK);
        ButterKnife.apply(editFields, ResetTextKeepStateSetter.UNFOCUSED);
        ButterKnife.apply(editFields, ResetTextKeepStateSetter.RESET);
    }

}
