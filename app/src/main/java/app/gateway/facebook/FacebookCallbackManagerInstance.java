package app.gateway.facebook;


import com.facebook.CallbackManager;

public class FacebookCallbackManagerInstance {
    private static CallbackManager facebookCallbackManager;

    public static CallbackManager get() {
        if (facebookCallbackManager == null)
            facebookCallbackManager = CallbackManager.Factory.create();
        return facebookCallbackManager;
    }
}
