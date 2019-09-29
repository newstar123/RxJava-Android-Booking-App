package app.delivering.component.service.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import app.CustomApplication;
import app.core.login.facebook.entity.LoginRequest;
import app.core.login.facebook.entity.LoginResponse;
import app.core.login.facebook.gateway.LoginGateway;
import app.core.logout.entity.LoginErrorException;
import app.delivering.component.authenticator.AuthenticatorActivity;
import app.delivering.component.authenticator.AuthenticatorActivityConstant;
import app.gateway.login.RetrofitLoginGateway;
import app.gateway.logout.LogoutAndroidAccountGateway;

public abstract class BaseAuthenticator extends AbstractAccountAuthenticator {
    private final AccountManager accountManager;
    private LoginGateway loginGateway;

    public BaseAuthenticator() {
        super(CustomApplication.get());
        accountManager = AccountManager.get(CustomApplication.get());
        loginGateway = new RetrofitLoginGateway();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(CustomApplication.get(), AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivityConstant.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivityConstant.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivityConstant.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        if (!authTokenType.equals(LogoutAndroidAccountGateway.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(LogoutAndroidAccountGateway.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }
        try {
            return tryGetAuthToken(account, authTokenType);
        } catch (LoginErrorException e) {
            return createLoginScreen(response, account, authTokenType);
        }
    }

    private Bundle tryGetAuthToken(Account account, String authTokenType) throws LoginErrorException {
        if (!accountExists(account.type))
            throw new LoginErrorException();
        String authToken = accountManager.peekAuthToken(account, authTokenType);
        String password = accountManager.getPassword(account);
        if (hasToLoadNewToken(authToken, password))
            authToken = getNewToken(account, authTokenType);
        if (!TextUtils.isEmpty(authToken))
            return returnToken(account, authToken);
        throw new LoginErrorException();
    }

    private boolean accountExists(String authTokenType) {
        try {
            return getAccount(authTokenType) != null;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Account getAccount(String authTokenType) throws SecurityException {
        Account[] accounts = accountManager.getAccountsByType(authTokenType);
        return accounts.length > 0 ? accounts[0] : null;
    }

    private Bundle returnToken(Account account, String authToken) {
        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        return result;
    }

    private boolean hasToLoadNewToken(String authToken, String password) {
        return TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(password);
    }

    private String getNewToken(Account account, String authTokenType) throws LoginErrorException {
        LoginRequest loginRequest = createLoginRequest(account);
        LoginResponse loginResponse = loginGateway.post(loginRequest).toBlocking().first();
        String token = loginResponse.getToken();
        if (!TextUtils.isEmpty(token))
            accountManager.setAuthToken(account, authTokenType, token);
        return token;
    }

    private LoginRequest createLoginRequest(Account account) {
        LoginRequest loginRequest = new LoginRequest();
        String password = accountManager.getPassword(account);
        loginRequest.setFacebookToken(password);
        return loginRequest;
    }

    private Bundle createLoginScreen(AccountAuthenticatorResponse response, Account account, String authTokenType) {
        final Intent intent = createAuthenticatorIntent(response, account, authTokenType);
        return createBundle(intent);
    }

    private Intent createAuthenticatorIntent(AccountAuthenticatorResponse response, Account account, String authTokenType) {
        Intent intent = new Intent(CustomApplication.get(), AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivityConstant.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AuthenticatorActivityConstant.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivityConstant.ARG_ACCOUNT_NAME, account.name);
        return intent;
    }

    private Bundle createBundle(Intent intent) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

}
