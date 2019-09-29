package app.delivering.mvp.verify.binder.init;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import app.R;
import app.core.profile.put.entity.PutProfileModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.verify.VerifyCodeFragment;
import app.delivering.component.verify.VerifyPhoneNumberActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.profile.edit.init.events.OnResumeProfileModelEvent;
import app.delivering.mvp.verify.events.VerificationEvent;
import app.delivering.mvp.verify.presenter.VerifyPhoneNumberPresenter;
import app.gateway.verify.phone.entity.number.StartPhoneRequestVerification;
import app.gateway.verify.phone.entity.number.StartPhoneResponseVerification;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class VerifyPhoneInitBinder extends BaseBinder {
    public static final String VALIDATED_COUNTRY_CODE = "country_code";
    public static final String VALIDATED_PHONE_NUMBER = "phone_number";
    private VerifyPhoneNumberPresenter verifyPhoneNumberPresenter;
    private InitExceptionHandler initExceptionHandler;

    @BindView(R.id.root_verify_phone_number) View rootLayout;
    @BindView(R.id.phone_number_input) EditText inputPhoneNumber;
    @BindView(R.id.privacy_policy_phone_number) TextView privacyPolicy;
    @BindView(R.id.submit_number_button) Button submitNumber;
    @BindView(R.id.invalid_phone_view) TextView invalidNumberInput;
    @BindView(R.id.phone_verification_progressbar) MaterialProgressBar progressBar;
    private Phonenumber.PhoneNumber numberProto;
    private PutProfileModel model;
    private String lastNumber;


    public VerifyPhoneInitBinder(BaseActivity activity) {
        super(activity);
        this.verifyPhoneNumberPresenter = new VerifyPhoneNumberPresenter(getActivity());
        this.initExceptionHandler = new InitExceptionHandler(getActivity());
        lastNumber = activity.getIntent().getStringExtra(VerifyPhoneNumberActivity.PHONE_NUMBER);
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
        inputPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        RxTextView.textChanges(inputPhoneNumber)
                .debounce(500, TimeUnit.MILLISECONDS)
                .skip(1)
                .observeOn(AndroidSchedulers.mainThread())
                .map(charSequence -> validate(charSequence.toString().trim()))
                .subscribe(this::updateUIState, throwable -> {}, () -> {});
        if (!TextUtils.isEmpty(lastNumber))
            updateUIState(validate(lastNumber.trim()));
    }

    private String validate(String text) {
        if (TextUtils.isEmpty(text)) return getString(R.string.empty_phone_number);
        try {
            throwInvalidPhone(text);
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return getString(R.string.invalid_phone_number);
        }
    }

    private void throwInvalidPhone(String phoneText) throws Exception {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = tm.getSimCountryIso().toUpperCase();
        Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneText, countryISO);
        if (!phoneUtil.isValidNumber(numberProto))
            throw new RuntimeException();
        else {
            this.numberProto = numberProto;
            String formattedPhone = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            inputPhoneNumber.setText(formattedPhone);
            inputPhoneNumber.setSelection(inputPhoneNumber.length());
        }
    }

    private void updateUIState(String validationResult) {
        invalidNumberInput.setText(validationResult);
        submitNumber.setEnabled(TextUtils.isEmpty(validationResult));
        invalidNumberInput.setVisibility(TextUtils.isEmpty(validationResult) ? View.INVISIBLE : View.VISIBLE);
    }

    @OnClick(R.id.submit_number_button) void submitPhoneNumber() {
        showProgress();
        sendVerificationEvent();
        StartPhoneRequestVerification request = new StartPhoneRequestVerification();
        request.setCountryCode("+" + String.valueOf(numberProto.getCountryCode()));
        request.setPhoneNumber(String.valueOf(numberProto.getNationalNumber()));
        updatePhoneInModel();

        verifyPhoneNumberPresenter.process(request)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::onError);
    }

    private void sendVerificationEvent() {
        EventBus.getDefault().postSticky(new VerificationEvent());
    }

    private void onError(Throwable throwable) {
        hideProgress();
        initExceptionHandler.showError(throwable, view -> submitPhoneNumber());
    }

    private void show(StartPhoneResponseVerification response) {
        hideProgress();
        if (response.getIsSuccess()) {
            VerifyCodeFragment verifyCodeFragment = new VerifyCodeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(VALIDATED_COUNTRY_CODE, "+" + String.valueOf(numberProto.getCountryCode()));
            bundle.putString(VALIDATED_PHONE_NUMBER, String.valueOf(numberProto.getNationalNumber()));
            verifyCodeFragment.setArguments(bundle);
            getActivity().start(verifyCodeFragment);
        }
    }

    private void updatePhoneInModel() {
        model = new PutProfileModel();
        model.setPhone(inputPhoneNumber.getText().toString());
        EventBus.getDefault().postSticky(new OnResumeProfileModelEvent(inputPhoneNumber.getText().toString()));
    }

    @OnClick(R.id.privacy_policy_phone_number) void privacyPolicy(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.privacy_policy)));
        getActivity().startActivity(intent);
    }
}
