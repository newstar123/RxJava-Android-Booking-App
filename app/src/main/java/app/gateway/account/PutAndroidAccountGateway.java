package app.gateway.account;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import app.core.login.facebook.entity.LoginResponse;
import app.core.login.facebook.gateway.PutLocalAccountGateway;
import app.delivering.component.BaseActivity;
import app.delivering.component.authenticator.AuthenticatorActivityConstant;
import app.gateway.auth.AndroidAuthTokenGateway;
import app.gateway.auth.PasswordTokenGateway;
import rx.Observable;

public class PutAndroidAccountGateway implements PutLocalAccountGateway {
    private final PasswordTokenGateway passwordTokenGateway;
    private BaseActivity activity;

    public PutAndroidAccountGateway(BaseActivity activity) {
        this.activity = activity;
        passwordTokenGateway = new PasswordTokenGateway();
    }

    @Override public Observable<Bundle> put(LoginResponse loginResponse) {
        Log.d("PutAndroidAccount", "------ put login response");
        Log.d("PutAndroidAccount", loginResponse.getToken());
        Log.d("PutAndroidAccount", String.valueOf(loginResponse.getId()));
        Bundle extra = new Bundle();
        extra.putString(AccountManager.KEY_ACCOUNT_NAME, String.valueOf(loginResponse.getId()));
        final String accountType = activity.getIntent().getStringExtra(AuthenticatorActivityConstant.ARG_ACCOUNT_TYPE);
        extra.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        String token = loginResponse.getToken();
        extra.putString(AccountManager.KEY_AUTHTOKEN, token);
        String value = passwordTokenGateway.get();
        extra.putString(AuthenticatorActivityConstant.PASSWORD_KEY, value);
        final Intent intent = new Intent();
        intent.putExtras(extra);
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AuthenticatorActivityConstant.PASSWORD_KEY);
        String type = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        final Account account = new Account(accountName, type);
        String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        String authtokenType = getAuthTokenType();
        AccountManager accountManager = AccountManager.get(activity);
        accountManager.addAccountExplicitly(account, accountPassword, null);
        accountManager.setAuthToken(account, authtokenType, authtoken);
        return Observable.just(extra);
    }

    private String getAuthTokenType() {
        String authTokenType = activity.getIntent().getStringExtra(AuthenticatorActivityConstant.ARG_AUTH_TYPE);
        if (authTokenType == null)
            authTokenType = AndroidAuthTokenGateway.AUTHTOKEN_TYPE_FULL_ACCESS;
        return authTokenType;
    }

}
