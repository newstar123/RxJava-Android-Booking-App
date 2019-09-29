package app.delivering.component.payment.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.payment.add.AddPaymentActivity;
import app.delivering.mvp.payment.list.actionbar.PaymentActionBarBinder;
import app.delivering.mvp.payment.list.delete.binder.PaymentsDeleteBinder;
import app.delivering.mvp.payment.list.init.binder.PaymentsInitBinder;
import app.delivering.mvp.payment.list.init.events.LoadPaymentsEvent;
import app.delivering.mvp.payment.list.init.events.OnDestroyPaymentsListEvent;
import app.delivering.mvp.payment.list.init.events.OnStartPaymentsFragmentEvent;
import app.delivering.mvp.payment.list.regular.binder.PaymentsRegularBinder;

public class PaymentsActivity extends BaseActivity {
    private boolean hasToReload;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payments);
        initUseCases();
    }

    private void initUseCases() {
        PaymentsInitBinder paymentsInitBinder = new PaymentsInitBinder(this);
        addToEventBusAndViewInjection(paymentsInitBinder);
        PaymentsRegularBinder paymentsRegularBinder = new PaymentsRegularBinder(this);
        addToEventBusAndViewInjection(paymentsRegularBinder);
        PaymentActionBarBinder paymentActionBarBinder = new PaymentActionBarBinder(this);
        addItemForViewsInjection(paymentActionBarBinder);
        PaymentsDeleteBinder paymentsDeleteBinder = new PaymentsDeleteBinder(this);
        addToEventBusAndViewInjection(paymentsDeleteBinder);
    }

    @Override public void onStart() {
        super.onStart();
        if (hasToReload){
            EventBus.getDefault().post(new LoadPaymentsEvent());
            hasToReload = false;
        }
        EventBus.getDefault().post(new OnStartPaymentsFragmentEvent());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.payments_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add_payment:
                    startActivityForResult(new Intent(this, AddPaymentActivity.class), AddPaymentActivity.PAYMENT_ADDED_CODE);
                    return true;
                case android.R.id.home:
                    onBackPressed();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddPaymentActivity.PAYMENT_ADDED_CODE && resultCode == Activity.RESULT_OK)
            hasToReload = true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.animation_stay, R.anim.animation_right_to_left);
    }

    @Override protected void onDestroy() {
        EventBus.getDefault().postSticky(new OnDestroyPaymentsListEvent());
        super.onDestroy();
    }
}
