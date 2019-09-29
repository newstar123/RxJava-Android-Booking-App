package app.delivering.mvp.main.init.binder.subbinder;

import java.util.concurrent.TimeUnit;

import app.core.init.token.entity.Token;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.main.init.binder.background.BackgroundStateListener;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.bluetooth.CheckBluetoothActiveGateway;
import rx.Observable;

public class MainInitLoginSubBinder {
    private BackgroundStateListener stateListener;

    public MainInitLoginSubBinder(BackgroundStateListener listener) {
        this.stateListener = listener;
    }

    public void checkUserLoggedIn(BaseActivity activity, boolean isBluetoothEnableActivityShowed){
        Observable.timer(300, TimeUnit.MILLISECONDS)
        .concatMap(aLong -> new AndroidAuthTokenGateway(activity).get())
                .concatMap(token -> checkBluetooth(token, isBluetoothEnableActivityShowed))
                .subscribe(token -> stateListener.onLoginStateVerified(token),
                        e -> stateListener.onLoginStateVerificationError(e),
                        () -> {});
    }

    private Observable<Token> checkBluetooth(Token token, boolean isBluetoothEnableActivityShowed) {
        return new CheckBluetoothActiveGateway().check()
                .onErrorResumeNext(throwable -> isBluetoothEnableActivityShowed ?
                            Observable.just(new EmptyResponse()) :
                            Observable.error(throwable)
                )
                .map(emptyResponse -> token);
    }
}
