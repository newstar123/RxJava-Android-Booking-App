package app.gateway.auth;


import com.facebook.AccessToken;

import app.core.login.facebook.entity.LoginResponse;

public class PasswordTokenGateway {

    public String get() {
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (currentAccessToken != null)
            return currentAccessToken.getToken();
        else
            return LoginResponse.GUEST_PASSWORD;
    }
}
