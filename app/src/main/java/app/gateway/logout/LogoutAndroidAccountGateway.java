package app.gateway.logout;

import android.accounts.Account;
import android.accounts.AccountManager;

import app.CustomApplication;
import app.R;
import app.core.logout.gateway.LogoutGateway;
import app.delivering.component.BaseActivity;
import rx.Observable;
import rx.Subscriber;

public class LogoutAndroidAccountGateway implements LogoutGateway {
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    private BaseActivity activity;

    public LogoutAndroidAccountGateway(BaseActivity activity) {
        this.activity = activity;
    }

    @Override public Observable<Object> logout() {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override public void call(Subscriber<? super Object> subscriber) {
                AccountManager accountManager = AccountManager.get(activity);
                invalidateToken(accountManager);
                Account[] accounts = accountManager.getAccountsByType(activity.getString(R.string.account_type));
                if (accounts.length == 0) {
                    onComplete(subscriber);
                    return;
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1)
                    accountManager.removeAccount(accounts[0],
                            activity,
                            future -> onComplete(subscriber),
                            null);
                else
                    accountManager.removeAccount(accounts[0],
                            future -> onComplete(subscriber),
                            null);
            }
        });
    }

    private void onComplete(Subscriber<? super Object> subscriber) {
        subscriber.onNext(new Object());
        subscriber.onCompleted();
    }

    private void invalidateToken(AccountManager accountManager) {
        String accountType = CustomApplication.get().getString(R.string.account_type);
        String token = getAuthToken(accountType, accountManager);
        accountManager.invalidateAuthToken(accountType, token);
    }

    private String getAuthToken(String accountType, AccountManager accountManager) throws SecurityException {
        Account[] accounts = accountManager.getAccountsByType(accountType);
        if (accounts.length == 0) return "";
        return accountManager.peekAuthToken(accounts[0], AUTHTOKEN_TYPE_FULL_ACCESS);
    }
}


