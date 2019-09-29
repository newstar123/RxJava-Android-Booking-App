package app.delivering.mvp.authenticator.login.facebook.binder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.R;
import app.core.login.facebook.entity.Younger21Exception;
import app.core.login.facebook.interactor.LoginFacebookInteractor;
import app.delivering.component.authenticator.AuthenticatorActivity;
import app.delivering.component.service.beacon.receiver.BluetoothStateReceiver;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.authenticator.init.events.OnStartAuthenticatorActivityEvent;
import app.delivering.mvp.authenticator.login.facebook.events.CheckBluetoothEvent;
import app.delivering.mvp.main.init.binder.InitExceptionHandler;
import app.delivering.mvp.main.init.events.UpdateMainActionBarEvent;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.ReplaySubject;

public class FacebookLoginBinder extends BaseBinder {
    @BindView(R.id.facebook_sign_up) RelativeLayout facebookSignUp;
    @BindView(R.id.continue_as_guest) TextView continueAsGuest;
    @BindView(R.id.login_container) RelativeLayout container;
    private final LoginFacebookInteractor loginFacebookInteractor;
    private final InitExceptionHandler initExceptionHandler;
    private AuthenticatorActivity activity;
    private ReplaySubject<Bundle> replaySubject;
    private Subscription loginSubscribe;

    public FacebookLoginBinder(AuthenticatorActivity activity) {
        super(activity);
        loginFacebookInteractor = new LoginFacebookInteractor(activity);
        initExceptionHandler = new InitExceptionHandler(activity);
        this.activity = activity;
        replaySubject = ReplaySubject.create();
    }

    @OnClick(R.id.facebook_sign_up) void facebookSignUp() {
        progressState(false);
        loginSubscribe = loginFacebookInteractor.process()
                .subscribe(replaySubject);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartEvent(OnStartAuthenticatorActivityEvent event) {
        if (loginSubscribe != null && !loginSubscribe.isUnsubscribed())
            progressState(true);
        replaySubject.asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.STOP))
                .subscribe(this::show, this::showError);
    }

    private void progressState(boolean showSpinningLoader) {
        if (showSpinningLoader)
            showProgress();

        container.setVisibility(View.GONE);
        facebookSignUp.setEnabled(false);
        continueAsGuest.setEnabled(false);
    }

    private void show(Bundle bundle) {
        EventBus.getDefault().post(new UpdateMainActionBarEvent());
        hideProgress();
        activity.setAccountAuthenticatorResult(bundle);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        EventBus.getDefault().postSticky(new CheckBluetoothEvent());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private void showError(Throwable throwable) {
        resetState();
        if (throwable instanceof Younger21Exception)
            initExceptionHandler.showError(throwable, view -> {});
        else
            initExceptionHandler.showError(throwable, view -> restart());
    }

    private void restart() {
        onStartEvent(new OnStartAuthenticatorActivityEvent());
        facebookSignUp();
    }

    private void resetState() {
        container.setVisibility(View.VISIBLE);
        hideProgress();
        replaySubject = ReplaySubject.create();
        onStartEvent(new OnStartAuthenticatorActivityEvent());
        facebookSignUp.setEnabled(true);
        continueAsGuest.setEnabled(true);
    }
}
