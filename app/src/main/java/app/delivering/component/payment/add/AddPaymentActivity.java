package app.delivering.component.payment.add;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.payment.add.add.request.binder.AddPaymentBinder;
import app.delivering.mvp.payment.add.add.request.events.OnAddPaymentStartEvent;
import app.delivering.mvp.payment.add.add.validate.binder.ValidateAddPaymentBinder;
import app.delivering.mvp.payment.add.capture.binder.PaymentCaptureBinder;
import app.delivering.mvp.payment.add.capture.events.PaymentCaptureEvent;
import app.delivering.mvp.payment.add.edit.cvv.OnCVVEditBinder;
import app.delivering.mvp.payment.add.edit.expired.OnExpiredEditBinder;
import app.delivering.mvp.payment.add.edit.number.binder.OnNumberEditBinder;
import app.delivering.mvp.payment.add.edit.zip.OnZipEditBinder;
import app.delivering.mvp.payment.add.init.binder.PaymentInitBinder;
import app.delivering.mvp.payment.add.profile.verification.binder.VerifyCardByProfilePhotoBinder;
import butterknife.BindView;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;

public class AddPaymentActivity extends BaseActivity {
    public static final int PAYMENT_ADDED_CODE = 3425;
    private static final int CARD_CAPTURE_REQUEST = 1277;
    public static final String CHECK_IN_WITHOUT_PAYMENT_KEY = "CHECK_IN_WITHOUT_PAYMENT";
    private PaymentCaptureEvent paymentCaptureEvent;

    @BindView(R.id.payment_add_type_image) ImageView capture_payment;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        initUseCases();
    }

    private void initUseCases() {
        addItemForViewsInjection(this);

        OnNumberEditBinder onNumberEditBinder = new OnNumberEditBinder(this);
        addItemForViewsInjection(onNumberEditBinder);
        OnZipEditBinder onZipEditBinder = new OnZipEditBinder(this);
        addItemForViewsInjection(onZipEditBinder);
        OnExpiredEditBinder onExpiredEditBinder = new OnExpiredEditBinder(this);
        addItemForViewsInjection(onExpiredEditBinder);
        OnCVVEditBinder binder = new OnCVVEditBinder(this);
        addItemForViewsInjection(binder);
        PaymentInitBinder paymentInitBinder = new PaymentInitBinder(this);
        addItemForViewsInjection(paymentInitBinder);
        AddPaymentBinder addPaymentBinder = new AddPaymentBinder(this);
        addToEventBusAndViewInjection(addPaymentBinder);
        ValidateAddPaymentBinder validateAddPaymentBinder = new ValidateAddPaymentBinder(this);
        addToEventBusAndViewInjection(validateAddPaymentBinder);
        PaymentCaptureBinder paymentCaptureBinder = new PaymentCaptureBinder(this);
        addToEventBusAndViewInjection(paymentCaptureBinder);
        VerifyCardByProfilePhotoBinder verifyCardByProfilePhotoBinder = new VerifyCardByProfilePhotoBinder(this);
        addToEventBus(verifyCardByProfilePhotoBinder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        EventBus.getDefault().post(new OnAddPaymentStartEvent(/*menu*/));
        return true;
    }

    @Override protected void onStart() {
        super.onStart();
        EventBus.getDefault().post(new OnAddPaymentStartEvent(/*menu*/));
        sendCaptureData();
    }

    private void sendCaptureData() {
        if (paymentCaptureEvent != null)
            EventBus.getDefault().post(paymentCaptureEvent);
        paymentCaptureEvent = null;
    }

    @OnClick(R.id.payment_add_type_image)
    public void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
        startActivityForResult(scanIntent, CARD_CAPTURE_REQUEST);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CARD_CAPTURE_REQUEST == requestCode && wasCaptureOk(data))
            paymentCaptureEvent = new PaymentCaptureEvent(data);
        if (CARD_CAPTURE_REQUEST == requestCode && !wasCaptureOk(data))
            Toast.makeText(this, R.string.scan_was_canceled, Toast.LENGTH_LONG).show();
    }

    private boolean wasCaptureOk(Intent data) {
        return data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT);
    }

    @Override public void onBackPressed() {
        if (getIntent().getBooleanExtra(AddPaymentActivity.CHECK_IN_WITHOUT_PAYMENT_KEY, false))
            setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
