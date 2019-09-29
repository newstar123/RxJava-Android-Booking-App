package app.delivering.mvp.payment.list.actionbar;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class PaymentActionBarBinder extends BaseBinder{
    @BindView(R.id.payments_toolbar) Toolbar toolBar;

    public PaymentActionBarBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle(getString(R.string.payment_title));
    }

}
