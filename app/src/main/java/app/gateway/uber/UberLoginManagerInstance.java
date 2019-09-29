package app.gateway.uber;


import android.app.Activity;
import android.content.Intent;

import com.uber.sdk.android.core.auth.LoginManager;

public class UberLoginManagerInstance {
    private static LoginManager loginManager;


    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (loginManager != null)
            loginManager.onActivityResult(activity, requestCode, resultCode, data);
    }

    public static void setLoginManager(LoginManager loginManager) {
        UberLoginManagerInstance.loginManager = loginManager;
    }

}
