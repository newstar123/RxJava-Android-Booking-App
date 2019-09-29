package app.delivering.component.service.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.os.Bundle;


public class Authenticator extends BaseAuthenticator {

    @Override public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
