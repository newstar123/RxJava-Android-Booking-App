package app.gateway.auth.context;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;

import java.io.IOException;

import app.R;
import app.core.init.token.entity.NoAccountException;
import app.core.init.token.entity.NoTokenException;
import app.core.init.token.entity.Token;
import app.core.init.token.gateway.AuthTokenGateway;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthTokenWithContextGateway implements AuthTokenGateway{
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    private final AccountManager accountManager;
    private final String accountType;
    private final String tokenType;

    public AuthTokenWithContextGateway(Context context) {
        this.accountType = context.getResources().getString(R.string.account_type);
        this.tokenType = AUTHTOKEN_TYPE_FULL_ACCESS;
        this.accountManager = AccountManager.get(context);
    }

    @Override public Observable<Token> get() {
        return get(null);
    }

    private Observable<Token> get(Bundle options) {
        return getSessionKey(options)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(getTokenFromFuture()).doOnNext(this::exceptionIfEmpty);
    }

    private void exceptionIfEmpty(Token token) {
        if (token.getAuthToken().isEmpty())
            throw new NoTokenException();
    }

    private Observable.Transformer<? super AccountManagerFuture<Bundle>, Token> getTokenFromFuture() {
        return accountManagerFutureObservable -> accountManagerFutureObservable.observeOn(Schedulers.io())
                .flatMap(AuthTokenWithContextGateway.this::createTokenObservable)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Token> createTokenObservable(AccountManagerFuture<Bundle> bundleAccountManagerFuture) {
        return Observable.create(new Observable.OnSubscribe<Bundle>() {
            @Override
            public void call(Subscriber<? super Bundle> subscriber) {
                try {
                    Bundle result = bundleAccountManagerFuture.getResult();
                    subscriber.onNext(result);
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.computation())
                .map(bundle -> new Token(bundle.getString(AccountManager.KEY_AUTHTOKEN)));
    }

    private Observable<AccountManagerFuture<Bundle>> getSessionKey(final Bundle options) {
        return Observable.create(new Observable.OnSubscribe<AccountManagerFuture<Bundle>>() {
            @Override
            public void call(Subscriber<? super AccountManagerFuture<Bundle>> subscriber) {
                try {
                    Account[] accounts = accountManager.getAccountsByType(accountType);
                    if (accounts.length == 0) {
                        subscriber.onError(new NoAccountException());
                        return;
                    }
                    AccountManagerFuture<Bundle> result = accountManager.getAuthToken(accounts[0], tokenType, options, false, null, null);
                    subscriber.onNext(result);
                }catch (SecurityException e){
                    e.printStackTrace();
                    subscriber.onError(new NoAccountException());
                    return;
                }
                subscriber.onCompleted();
            }
        });
    }

}
