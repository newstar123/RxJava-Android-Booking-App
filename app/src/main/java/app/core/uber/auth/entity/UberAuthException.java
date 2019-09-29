package app.core.uber.auth.entity;


import com.uber.sdk.android.core.auth.AuthenticationError;

public class UberAuthException extends RuntimeException {
    private AuthenticationError error;

    public UberAuthException(AuthenticationError error) {
        this.error = error;
    }

    public AuthenticationError getError() {
        return error;
    }
}
