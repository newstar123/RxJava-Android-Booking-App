package app.delivering.mvp.verify.binder.init;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.mixpanel.android.util.StringUtils;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseFragment;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.verify.binder.event.VerifyCodeEvent;
import app.delivering.mvp.verify.presenter.VerifyCodeNumberPresenter;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.rest.client.twilio.TwilioRx401Policy;
import app.gateway.verify.phone.entity.code.CheckPhoneRequestVerification;
import app.gateway.verify.phone.entity.code.CheckPhoneResponseVerification;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VerifyCodeInitBinder extends BaseBinder {
    private static int TIMER_VAL_IN_SEC = 60;

    private VerifyCodeNumberPresenter verifyCodeNumberPresenter;
    private CheckPhoneRequestVerification checkPhoneRequestVerification;
    private InitExceptionHandler initExceptionHandler;
    private Subscription timerSubscription;
    private int currTimerVal = 0;
    private BaseFragment baseFragment;
    private LockButtonTimerInterface lockButtonTimerInterface;
    private int diffInSec;
    private int timerValFromCache;

    @BindView(R.id.root_verification_code) View rootLayout;
    @BindView(R.id.verification_code_layout) TextInputLayout inputCodeLayout;
    @BindView(R.id.verification_code_input) EditText inputCodeView;
    @BindView(R.id.submit_code_button) Button submitButton;
    @BindView(R.id.invalid_code_view) TextView invalidCodeInput;
    @BindView(R.id.code_verification_progressbar) MaterialProgressBar progressBar;

    public VerifyCodeInitBinder(BaseFragment fragment) {
        super(fragment.getBaseActivity());
        baseFragment = fragment;
        this.verifyCodeNumberPresenter = new VerifyCodeNumberPresenter(getActivity());
        lockButtonTimerInterface = this.verifyCodeNumberPresenter;
        initExceptionHandler = new InitExceptionHandler(getActivity());
        this.checkPhoneRequestVerification = new CheckPhoneRequestVerification();
        if (fragment.getArguments() != null) {
            String phone = fragment.getArguments().getString(VerifyPhoneInitBinder.VALIDATED_PHONE_NUMBER, "");
            checkPhoneRequestVerification.setPhoneNumber(phone);
            String countryCode = fragment.getArguments().getString(VerifyPhoneInitBinder.VALIDATED_COUNTRY_CODE, "");
            checkPhoneRequestVerification.setCountryCode(countryCode);
        }
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
        RxTextView.textChanges(inputCodeView)
                .debounce(500, TimeUnit.MILLISECONDS)
                .skip(1)
                .map(charSequence -> TextUtils.isEmpty(charSequence.toString().trim()) ? getString(R.string.invalid_code) : "")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUIState, throwable -> {}, () -> {});
        inputCodeLayout.requestFocusFromTouch();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVerifyCodeEvent(VerifyCodeEvent event) {
        getTimerPreConditions();
    }

    private void getTimerPreConditions() {
        diffInSec = lockButtonTimerInterface.getCurrTimeValInSec() - lockButtonTimerInterface.getSavedDeviceTimeInSec();
        timerValFromCache = lockButtonTimerInterface.getSavedTimerValInSec();

        if (diffInSec < timerValFromCache) {
            submitButton.setEnabled(false);
            setUpTimer(timerValFromCache - diffInSec);
        }
    }

    private void setUpTimer(int val) {
        timerSubscription = Observable.zip(Observable.range(0, val), Observable.interval(1, TimeUnit.SECONDS),
                (rangeValue, interValue) -> val - rangeValue)
                .compose(baseFragment.bindUntilEvent(FragmentEvent.PAUSE))
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        getActivity().runOnUiThread(() -> {
                            submitButton.setText(getString(R.string.button_submit));
                            submitButton.setEnabled(true);
                            lockButtonTimerInterface.saveTimerValInSec(currTimerVal);
                            lockButtonTimerInterface.saveDeviceTimeInSec(lockButtonTimerInterface.getCurrTimeValInSec());
                            if (!timerSubscription.isUnsubscribed())
                                timerSubscription.unsubscribe();
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer integer) {
                        currTimerVal = integer;
                        getActivity().runOnUiThread(() -> submitButton.setText(String.format(Locale.getDefault(),
                                "%s %d", getString(R.string.submit_code_text), integer)));
                    }
                });
    }

    private void updateUIState(String validationResult) {
        invalidCodeInput.setText(validationResult);
        invalidCodeInput.setVisibility(TextUtils.isEmpty(validationResult) ? View.INVISIBLE : View.VISIBLE);

        if (!submitButton.getText().toString().toLowerCase().contains(getString(R.string.submit_code_text)))
            submitButton.setEnabled(TextUtils.isEmpty(validationResult));
    }

    @OnClick(R.id.submit_code_button) void submitCode() {
        showProgress();
        submitButton.setEnabled(false);
        checkPhoneRequestVerification.setVerificationCode(inputCodeView.getText().toString());
        verifyCodeNumberPresenter.process(checkPhoneRequestVerification)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError);
    }

    private void show(CheckPhoneResponseVerification response) {
        hideProgress();
        if (response.getIsSuccess()) {
            MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getPhoneResolvedEvent());
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    private void onError(Throwable throwable) {
        hideProgress();
        if (throwable instanceof HttpException && ((HttpException) throwable).code() == 401 &&
                ((HttpException) throwable).message().equals(TwilioRx401Policy.Unauthorized)) {

            updateUIState(getString(R.string.wrong_code));
            setUpTimer(TIMER_VAL_IN_SEC);
        } else
            initExceptionHandler.showError(throwable, view -> {} /*submitCode()*/);
    }

}
