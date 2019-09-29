package app.delivering.mvp.verify.binder.init;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.verify.VerifyCodeFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.verify.presenter.VerifyPhoneNumberPresenter;
import app.gateway.verify.phone.entity.number.StartPhoneRequestVerification;
import app.gateway.verify.phone.entity.number.StartPhoneResponseVerification;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ResendPhoneVerificationCodeBinder extends BaseBinder {
    private static final int COUNT_DOWN = 30;
    private VerifyPhoneNumberPresenter verifyPhoneNumberPresenter;
    private InitExceptionHandler initExceptionHandler;
    private StartPhoneRequestVerification request;

    @BindView(R.id.resend_verification_code_progress) MaterialProgressBar progressBar;
    @BindView(R.id.resend_verification_code_button) TextView resendText;
    @BindView(R.id.verification_code_input) EditText inputCodeView;
    @BindView(R.id.submit_code_button) Button verifyButton;

    public ResendPhoneVerificationCodeBinder(VerifyCodeFragment fragment) {
        super(fragment.getBaseActivity());
        this.verifyPhoneNumberPresenter = new VerifyPhoneNumberPresenter(getActivity());
        this.initExceptionHandler = new InitExceptionHandler(getActivity());
        request = new StartPhoneRequestVerification();
        if (fragment.getArguments() != null) {
            String phone = fragment.getArguments().getString(VerifyPhoneInitBinder.VALIDATED_PHONE_NUMBER, "");
            request.setPhoneNumber(phone);
            String countryCode = fragment.getArguments().getString(VerifyPhoneInitBinder.VALIDATED_COUNTRY_CODE, "");
            request.setCountryCode(countryCode);
        }
    }

    @Override public void afterViewsBounded() {
        startCountdownTimer();
    }

    @OnClick(R.id.resend_verification_code_button) void resend(){
        if (resendText.getText().toString().equals(getString(R.string.re_send))) {
            resendText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            verifyButton.setEnabled(false);
            inputCodeView.setText("");
            inputCodeView.setSelection(inputCodeView.length());
            verifyPhoneNumberPresenter.process(request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                    .subscribe(this::show, this::onError, this::resetState);
        }
    }

    private void show(StartPhoneResponseVerification response) {
        progressBar.setVisibility(View.GONE);
        startCountdownTimer();
    }

    private void startCountdownTimer() {
        resendText.setEnabled(false);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumIntegerDigits(2);
        Observable.interval(1, TimeUnit.SECONDS)
                .take(COUNT_DOWN)
                .map(v -> COUNT_DOWN - v)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> resendText.setText(String.format(getString(R.string.re_send_timer), decimalFormat.format(count))),
                        e -> {}, () -> {
                            resendText.setText(R.string.re_send);
                            resendText.setEnabled(true);
                        });
    }

    private void onError(Throwable throwable) {
        initExceptionHandler.showError(throwable, view -> {});
        resetState();
    }

    private void resetState() {
        resendText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        resendText.setEnabled(true);
    }

}
