package app.delivering.mvp.verify.binder.init;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import app.R;
import app.core.profile.put.entity.PutProfileModel;
import app.core.verify.interactor.mail.entity.EmailAlreadyInUseException;
import app.delivering.component.verify.VerifyEmailActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.init.events.UpdateMainActionBarEvent;
import app.delivering.mvp.profile.drawer.init.events.OpenNavigationDrawerEvent;
import app.delivering.mvp.verify.presenter.VerifyEmailPresenter;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;

public class VerifyEmailInitBinder extends BaseBinder {
    private VerifyEmailPresenter verifyEmailPresenter;
    private InitExceptionHandler initExceptionHandler;
    private PutProfileModel model;
    private final RxDialogHandler rxDialogHandler;

    @BindView(R.id.root_verify_email) View rootLayout;
    @BindView(R.id.email_address_input) EditText inputEmail;
    @BindView(R.id.privacy_policy_email) TextView privacyPolicy;
    @BindView(R.id.submit_email_button) Button submitEmail;
    @BindView(R.id.invalid_email_address) TextView invalidEmailInput;
    @BindView(R.id.email_verification_progressbar) MaterialProgressBar progressBar;


    public VerifyEmailInitBinder(VerifyEmailActivity activity) {
        super(activity);
        verifyEmailPresenter = new VerifyEmailPresenter(getActivity());
        initExceptionHandler = new InitExceptionHandler(getActivity());
        rxDialogHandler = new RxDialogHandler(getActivity());
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
        RxTextView.textChanges(inputEmail)
                .debounce(500, TimeUnit.MILLISECONDS)
                .skip(1)
                .map(charSequence -> validate(charSequence.toString().trim()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateUIState, throwable -> {}, () -> {});
    }

    private String validate(String text) {
        if (TextUtils.isEmpty(text)) return getString(R.string.empty_email_address);
        return Patterns.EMAIL_ADDRESS.matcher(text).matches() ? "" : getString(R.string.invalid_email_address);
    }

    private void updateUIState(String validationResult) {
        invalidEmailInput.setText(validationResult);
        submitEmail.setEnabled(TextUtils.isEmpty(validationResult));
        invalidEmailInput.setVisibility(TextUtils.isEmpty(validationResult) ? View.INVISIBLE : View.VISIBLE);
    }

    @OnClick(R.id.submit_email_button) void submitEmail() {
        showProgress();
        model = new PutProfileModel();
        model.setEmail(inputEmail.getText().toString());
        verifyEmailPresenter.process(model)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(loginResponse -> dialogWithOptions(), this::onError);
    }

    private void dialogWithOptions() {
        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getEmailVerificationResolvedEvent());
        rxDialogHandler.showTwoButtonsWithTitle(
                R.string.pending_email_verification,
                R.string.check_email_inbox,
                R.string.change_email_button,
                R.string.got_it)
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
                    if (result) show();
                    else rxDialogHandler.dismissDialog();
                });
    }

    private void show() {
        hideProgress();
        InputMethodManager inputMethod = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethod.hideSoftInputFromWindow(inputEmail.getWindowToken(), 0);

        sendUpdates();
        getActivity().onBackPressed();
    }

    private void sendUpdates() {
        EventBus.getDefault().postSticky(new UpdateMainActionBarEvent());
        EventBus.getDefault().postSticky(new OpenNavigationDrawerEvent());
    }

    private void onError(Throwable throwable) {
        hideProgress();
        if (throwable instanceof HttpException && ((HttpException) throwable).code() == 409) {
            initExceptionHandler.showErrorWithDuration(new EmailAlreadyInUseException(), v -> {});
            inputEmail.setText("");
            inputEmail.setSelection(inputEmail.length());
        } else
            initExceptionHandler.showError(throwable, view -> submitEmail());
    }

    @OnClick(R.id.privacy_policy_email) void privacyPolicy() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.privacy_policy)));
        getActivity().startActivity(intent);
    }

}
