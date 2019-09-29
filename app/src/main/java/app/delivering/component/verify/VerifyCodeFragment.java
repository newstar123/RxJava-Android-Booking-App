package app.delivering.component.verify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.verify.binder.actionbar.VerifyCodeActionBarBinder;
import app.delivering.mvp.verify.binder.event.VerifyCodeEvent;
import app.delivering.mvp.verify.binder.init.ResendPhoneVerificationCodeBinder;
import app.delivering.mvp.verify.binder.init.VerifyCodeInitBinder;

public class VerifyCodeFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verification_code, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        EventBus.getDefault().post(new VerifyCodeEvent());
        super.onResume();
    }

    private void initViews(View view) {
        VerifyCodeActionBarBinder verifyCodeActionBarBinder = new VerifyCodeActionBarBinder(getBaseActivity());
        addItemForViewsInjection(verifyCodeActionBarBinder, view);
        VerifyCodeInitBinder verifyCodeInitBinder = new VerifyCodeInitBinder(this);
        addToEventBusAndViewInjection(verifyCodeInitBinder, view);
        ResendPhoneVerificationCodeBinder resendPhoneVerificationCodeBinder = new ResendPhoneVerificationCodeBinder(this);
        addItemForViewsInjection(resendPhoneVerificationCodeBinder, view);
    }
}
