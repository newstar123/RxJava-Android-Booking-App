package app.delivering.component.ride.auth.utils;

import com.uber.sdk.android.core.auth.AuthenticationError;



public class LoginAuthenticationException extends Exception{
    private AuthenticationError authenticationError;

    /**
     * Construct the exception with an {@link AuthenticationError}
     */
    LoginAuthenticationException(AuthenticationError error) {
        super(error.toString());

        authenticationError = error;
    }

    /**
     * Gets the {@link AuthenticationError} for the exception
     */
    public AuthenticationError getAuthenticationError() {
        return authenticationError;
    }
}
