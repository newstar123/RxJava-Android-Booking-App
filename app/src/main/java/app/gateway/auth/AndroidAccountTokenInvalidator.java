package app.gateway.auth;


import android.accounts.Account;
import android.accounts.AccountManager;

import app.CustomApplication;
import app.R;
import rx.Observable;
import rx.Subscriber;

public class AndroidAccountTokenInvalidator {

    public static Observable<Object> invalidateToken(){
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override public void call(Subscriber<? super Object> subscriber) {
                String accountType = CustomApplication.get().getString(R.string.account_type);
                String token = getAuthToken(accountType);
                AccountManager.get(CustomApplication.get()).invalidateAuthToken(accountType, token);
                subscriber.onNext(new Object());
                subscriber.onCompleted();
            }
        });
    }

    private static String getAuthToken(String accountType) throws SecurityException{
        AccountManager accountManager = AccountManager.get(CustomApplication.get());
        Account[] accounts = accountManager.getAccountsByType(accountType);
        return accountManager.peekAuthToken(accounts[0], AndroidAuthTokenGateway.AUTHTOKEN_TYPE_FULL_ACCESS);
    }
}
