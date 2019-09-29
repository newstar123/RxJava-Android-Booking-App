package app.gateway.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
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

public class AndroidAuthTokenGateway implements AuthTokenGateway{
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    private final AccountManager accountManager;
    private final Activity activity;
    private final String accountType;
    private final String tokenType;
    private final String[] requiredFeatures;

    public AndroidAuthTokenGateway(Activity activity) {
        this.activity = activity;
        this.accountType = activity.getString(R.string.account_type);
        this.tokenType = AUTHTOKEN_TYPE_FULL_ACCESS;
        this.requiredFeatures = null;
        this.accountManager = AccountManager.get(activity.getApplicationContext());
    }

    @Override public Observable<Token> get() {
        return get(null);
    }

    private Observable<Token> get(Bundle options) {
        return getSessionKey(options)
                .subscribeOn(AndroidSchedulers.mainThread())
                .compose(getTokenFromFuture())
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof NoAccountException) {
                        return addAccountAndGetToken(options);
                    } else
                        return Observable.error(throwable.getCause());
                }).doOnNext(this::exceptionIfEmpty);
    }

    private Observable<Token> addAccountAndGetToken(final Bundle options) {
        return addAccount(options)
                .concatMap(bundleAccountManagerFuture -> getSessionKey(null))
                .compose(getTokenFromFuture());
    }

    private void exceptionIfEmpty(Token token) {
        if (token.getAuthToken().isEmpty())
            throw new NoTokenException();
    }

    private Observable.Transformer<? super AccountManagerFuture<Bundle>, Token> getTokenFromFuture() {
        return accountManagerFutureObservable -> accountManagerFutureObservable.observeOn(Schedulers.io())
                .flatMap(AndroidAuthTokenGateway.this::createTokenObservable)
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
                    AccountManagerFuture<Bundle> result = accountManager.getAuthToken(accounts[0], tokenType, options, activity, null, null);
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

    private Observable<AccountManagerFuture<Bundle>> addAccount(final Bundle options) {
        return Observable.create(new Observable.OnSubscribe<AccountManagerFuture<Bundle>>() {
            @Override
            public void call(Subscriber<? super AccountManagerFuture<Bundle>> subscriber) {
                AccountManagerFuture<Bundle> result;
                result = accountManager.addAccount(accountType, tokenType, requiredFeatures, options, activity, null, null);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }


}
