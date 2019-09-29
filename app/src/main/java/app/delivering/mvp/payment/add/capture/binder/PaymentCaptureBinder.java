package app.delivering.mvp.payment.add.capture.binder;


import android.widget.EditText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.payment.add.capture.events.PaymentCaptureEvent;
import butterknife.BindView;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.Observable;

public class PaymentCaptureBinder extends BaseBinder {
    @BindView(R.id.payment_add_number_input) EditText paymentNumberEditText;

    public PaymentCaptureBinder(BaseActivity activity) {
        super(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPaymentSuccess(PaymentCaptureEvent model) {
        CreditCard scanResult = model.getData().getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
        paymentNumberEditText.setText("");
        String[] cardNumberByLetters = scanResult.cardNumber.split("");
        Observable.from(cardNumberByLetters).subscribe(letter -> paymentNumberEditText.append(letter));

    }
}
