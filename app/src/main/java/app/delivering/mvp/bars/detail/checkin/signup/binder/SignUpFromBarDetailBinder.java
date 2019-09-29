package app.delivering.mvp.bars.detail.checkin.signup.binder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.login.facebook.entity.Younger21Exception;
import app.core.login.facebook.interactor.LoginFacebookInteractor;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.authenticator.init.events.OnFinishAuthenticatorActivityEvent;
import app.delivering.mvp.bars.detail.checkin.click.events.OpenTabEvent;
import app.delivering.mvp.bars.detail.checkin.signup.events.SignUpFromBarDetailEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import butterknife.BindView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.android.schedulers.AndroidSchedulers;

public class SignUpFromBarDetailBinder extends BaseBinder {
    @BindView(R.id.bar_detail_root_progressbar) MaterialProgressBar progressBar;
    @BindView(R.id.see_who_is_here_authorization_button) Button authorizationButton;
    private BarDetailActivity activity;
    private final LoginFacebookInteractor loginFacebookInteractor;
    private final InitExceptionHandler initExceptionHandler;

    public SignUpFromBarDetailBinder(BarDetailActivity activity) {
        super(activity);
        this.activity = activity;
        loginFacebookInteractor = new LoginFacebookInteractor(getActivity());
        initExceptionHandler = new InitExceptionHandler(activity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void load(SignUpFromBarDetailEvent event) {
        if (progressBar != null) {
            setProgress(progressBar);
            showProgress();
        }
        setButtonsEnabled(false);
        loginFacebookInteractor.process()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(bundle -> show(bundle, event.isOpenTabCall()), this::showError);
    }

    private void show(Bundle bundle, boolean isOpenTabCall) {
        setButtonsEnabled(true);
        activity.setAccountAuthenticatorResult(bundle);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        EventBus.getDefault().postSticky(new OnFinishAuthenticatorActivityEvent());
        if (isOpenTabCall)
            EventBus.getDefault().postSticky(new OpenTabEvent());
        hideProgress();
    }

    private void showError(Throwable throwable) {
        hideProgress();
        if (throwable instanceof Younger21Exception)
            initExceptionHandler.showError(throwable, view -> {});
        setButtonsEnabled(true);
    }

    private void setButtonsEnabled(boolean isEnabled) {
        if (authorizationButton != null)
            authorizationButton.setEnabled(isEnabled);
    }
}
