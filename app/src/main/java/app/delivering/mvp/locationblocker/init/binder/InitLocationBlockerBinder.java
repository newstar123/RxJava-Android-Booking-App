package app.delivering.mvp.locationblocker.init.binder;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.delivering.mvp.locationblocker.init.presenter.InitLocationBlockerPresenter;
import butterknife.BindView;
import butterknife.OnClick;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class InitLocationBlockerBinder extends BaseBinder {
    @BindView(R.id.location_blocker_root_layout) View locationBlockerRoot;
    @BindView(R.id.location_blocker_enter_email) EditText emailInput;
    @BindView(R.id.location_blocker_share_email) Button shareEmail;
    @BindView(R.id.invalid_email_address) TextView textView;
    @BindView(R.id.email_verification_progressbar) MaterialProgressBar progressBar;

    private InitLocationBlockerPresenter presenter;

    public InitLocationBlockerBinder(BaseActivity activity) {
        super(activity);
        presenter = new InitLocationBlockerPresenter(activity);
    }

    @Override public void afterViewsBounded() {
        setProgress(progressBar);
        RxTextView.textChanges(emailInput)
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
        textView.setText(validationResult);
        shareEmail.setEnabled(TextUtils.isEmpty(validationResult));
        textView.setVisibility(TextUtils.isEmpty(validationResult) ? View.INVISIBLE : View.VISIBLE);
    }

    @OnClick(R.id.location_blocker_share_email) void setShareEmail() {
        showProgress();
        presenter.process(emailInput.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> new RxDialogHandler(getActivity())
                                .showOneButtonWithoutTitle(R.string.success_message_for_location_blocker, R.string.ok)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(isOk -> {}, e->{},()->{}),
                        e->hideProgress(),
                        this::hideProgress);
    }
}