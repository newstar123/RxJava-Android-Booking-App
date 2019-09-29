package app.core.uber.auth.entity;


import com.uber.sdk.rides.client.AccessTokenSession;

public class UberAuthToken {

    private AccessTokenSession session;

    public void setSession(AccessTokenSession session) {
        this.session = session;
    }

    public AccessTokenSession getSession() {
        return session;
    }
}
