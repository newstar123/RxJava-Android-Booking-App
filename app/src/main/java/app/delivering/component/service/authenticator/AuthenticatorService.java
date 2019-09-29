package app.delivering.component.service.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    private Authenticator authenticator;

    @Override public void onCreate() {
        super.onCreate();
        authenticator = new Authenticator();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
