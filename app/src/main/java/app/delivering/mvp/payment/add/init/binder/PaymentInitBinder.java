package app.delivering.mvp.payment.add.init.binder;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;


public class PaymentInitBinder extends BaseBinder {
    @BindView(R.id.add_payment_toolbar) Toolbar toolBar;

    public PaymentInitBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        super.afterViewsBounded();
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle(getString(R.string.add_payment_title));
    }
}
