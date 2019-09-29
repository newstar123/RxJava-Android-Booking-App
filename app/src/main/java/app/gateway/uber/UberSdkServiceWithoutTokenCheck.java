package app.gateway.uber;


import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.rides.client.AccessTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.services.RidesService;

import app.CustomApplication;

public class UberSdkServiceWithoutTokenCheck {

    public static RidesService create(){
       AccessTokenManager accessTokenManager = new AccessTokenManager(CustomApplication.get());
        SessionConfiguration uberSession = UberSdk.getDefaultSessionConfiguration();
        AccessTokenSession accessTokenSession = new AccessTokenSession(uberSession, accessTokenManager);
        return UberRidesApi.with(accessTokenSession).build().createService();
    }
}
