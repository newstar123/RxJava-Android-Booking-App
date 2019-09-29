package app.delivering.component.ride.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.android.core.auth.ResponseType;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Locale;

import app.delivering.component.BaseActivity;
import app.delivering.component.ride.auth.utils.AuthUtils;
import app.delivering.component.ride.auth.utils.LoginAuthenticationException;

import static app.delivering.component.ride.auth.utils.AuthUtils.EXTRA_CODE_RECEIVED;
import static app.delivering.component.ride.auth.utils.AuthUtils.EXTRA_ERROR;


public class RedirectAuthActivity extends BaseActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.uber.sdk.android.core.R.layout.ub__login_activity);
        webView = (WebView) findViewById(com.uber.sdk.android.core.R.id.ub__login_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        SessionConfiguration defaultSessionConfiguration = UberSdk.getDefaultSessionConfiguration();
        final String redirectUri = defaultSessionConfiguration.getRedirectUri();
        webView.setWebViewClient(new AuthorizationCodeClient(redirectUri));
        webView.loadUrl(buildUrl(redirectUri, ResponseType.CODE, defaultSessionConfiguration));
    }

    @NonNull
    @VisibleForTesting
    String buildUrl(
            @NonNull String redirectUri,
            @NonNull ResponseType responseType,
            @NonNull SessionConfiguration configuration) {

        final String CLIENT_ID_PARAM = "client_id";
        final String ENDPOINT = "login";
        final String HTTPS = "https";
        final String PATH = "oauth/v2/authorize";
        final String REDIRECT_PARAM = "redirect_uri";
        final String RESPONSE_TYPE_PARAM = "response_type";
        final String SCOPE_PARAM = "scope";
        final String SHOW_FB_PARAM = "show_fb";
        final String SIGNUP_PARAMS = "signup_params";
        final String REDIRECT_LOGIN = "{\"redirect_to_login\":true}";



        Uri.Builder builder = new Uri.Builder();
        builder.scheme(HTTPS)
                .authority(ENDPOINT + "." + configuration.getEndpointRegion().getDomain())
                .appendEncodedPath(PATH)
                .appendQueryParameter(CLIENT_ID_PARAM, configuration.getClientId())
                .appendQueryParameter(REDIRECT_PARAM, redirectUri)
                .appendQueryParameter(RESPONSE_TYPE_PARAM, responseType.toString().toLowerCase(Locale.US))
                .appendQueryParameter(SCOPE_PARAM, getScopes(configuration))
                .appendQueryParameter(SHOW_FB_PARAM, "false")
                .appendQueryParameter(SIGNUP_PARAMS, app.delivering.component.ride.auth.utils.AuthUtils.createEncodedParam(REDIRECT_LOGIN));

        return builder.build().toString();
    }

    private String getScopes(SessionConfiguration configuration) {
        String scopes = AuthUtils.scopeCollectionToString(configuration.getScopes());
        if (!configuration.getCustomScopes().isEmpty()) {
            scopes =  AuthUtils.mergeScopeStrings(scopes,
                    AuthUtils.customScopeCollectionToString(configuration.getCustomScopes()));
        }
        return scopes;
    }

    class AuthorizationCodeClient extends OAuthWebViewClient {

        public AuthorizationCodeClient(@NonNull String redirectUri) {
            super(redirectUri);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.startsWith(redirectUri)) {
                onCodeReceived(Uri.parse(url));
            }
        }
    }

    void onCodeReceived(Uri uri) {
        try {
            String code = AuthUtils.parseAuthorizationCode(uri);

            setResult(RESULT_OK, new Intent().putExtra(EXTRA_CODE_RECEIVED, code));
            finish();
        } catch (LoginAuthenticationException loginException) {
            onError(loginException.getAuthenticationError());
            return;
        }
    }

    void onError(@NonNull AuthenticationError error) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ERROR, error.toStandardString());
        setResult(RESULT_CANCELED, data);
        finish();
    }

    abstract class OAuthWebViewClient extends WebViewClient {

        protected static final String ERROR = "error";

        @NonNull
        protected final String redirectUri;

        /**
         * Initialize the {@link WebView} client.
         *
         * @param redirectUri the redirect URI {@link String} that will contain the access token information
         */
        public OAuthWebViewClient(@NonNull String redirectUri) {
            this.redirectUri = redirectUri;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

        }
    }
}
