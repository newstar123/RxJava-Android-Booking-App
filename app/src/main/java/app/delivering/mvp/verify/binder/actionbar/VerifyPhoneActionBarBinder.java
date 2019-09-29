package app.delivering.mvp.verify.binder.actionbar;

import android.support.v7.widget.Toolbar;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class VerifyPhoneActionBarBinder extends BaseBinder {
    @BindView(R.id.verify_phone_number_toolbar) Toolbar toolBar;
    private VerifyPhoneNumberActivity verifyPhoneNumberActivity;

    public VerifyPhoneActionBarBinder(VerifyPhoneNumberActivity activity) {
        super(activity);
        verifyPhoneNumberActivity = activity;
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle("");

        toolBar.setNavigationIcon(R.drawable.inset_white_cross);
        toolBar.setNavigationOnClickListener(click -> {
            verifyPhoneNumberActivity.setResult(VerifyPhoneNumberActivity.PHONE_VERIFICATION_EMPTY_RESULT);
            getActivity().finish();
        });
    }
}
