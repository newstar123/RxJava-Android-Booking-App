package app.delivering.component.ride.auth.utils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.uber.sdk.android.core.auth.AuthenticationError;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.Scope;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alexey on 18.06.2017.
 */

public class AuthUtils {
    static final String KEY_EXPIRATION_TIME = "expires_in";
    static final String KEY_SCOPES = "scope";
    static final String KEY_TOKEN = "access_token";
    static final String KEY_REFRESH_TOKEN = "refresh_token";
    static final String KEY_TOKEN_TYPE = "token_type";
    /**
     * Used to retrieve the {@link AuthenticationError} from an {@link Intent}.
     */
    public static final String EXTRA_ERROR = "ERROR";

    /**
     * Used to retrieve the Access Token from an {@link Intent}.
     */
    static final String EXTRA_ACCESS_TOKEN = "ACCESS_TOKEN";

    /**
     * Used to retrieve the Refresh Token from an {@link Intent}.
     */
    static final String EXTRA_REFRESH_TOKEN = "REFRESH_TOKEN";

    /**
     * Used retrieve the {@link Scope}s granted from from an {@link Intent}.
     */
    static final String EXTRA_SCOPE = "SCOPE";

    /**
     * Used to retrieve the token expiry from an {@link Intent}.
     */
    static final String EXTRA_EXPIRES_IN = "EXPIRES_IN";

    /**
     * Used to retrieve the token type from an {@link Intent}.
     */
    static final String EXTRA_TOKEN_TYPE = "TOKEN_TYPE";

    /**
     * Used to indicate that an authorization code has been received from an {@link Intent}.
     */
    public static final String EXTRA_CODE_RECEIVED = "CODE_RECEIVED";

    static final int REQUEST_CODE_LOGIN_DEFAULT = 1001;


    /**
     * @param scopeCollection
     * @return true if any {@link com.uber.sdk.core.auth.Scope}s requested is {@link com.uber.sdk.core.auth.Scope.ScopeType#PRIVILEGED}
     */
    static boolean isPrivilegeScopeRequired(@NonNull Collection<Scope> scopeCollection) {
        for (Scope scope : scopeCollection) {
            if (scope.getScopeType().equals(Scope.ScopeType.PRIVILEGED)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a {@link Collection} of {@link Scope}s to a {@link Set} of {@link String}s.
     *
     * @param scopeCollection the {@link Collection} of {@link Scope}s to convert.
     * @return a {@link Set} of {@link String}s.
     */
    @NonNull
    static Set<String> scopeCollectionToStringSet(@NonNull Collection<Scope> scopeCollection) {
        Set<String> stringCollection = new HashSet<>();
        for (Scope scope : scopeCollection) {
            stringCollection.add(scope.name());
        }

        return stringCollection;
    }

    /**
     * Converts a {@link String} representing space delimited {@link Scope}s to a {@link Collection<Scope>}.
     *
     * @param scopesString the {@link String} to convert.
     * @return a {@link Collection} of {@link Scope}s.
     * @throws IllegalArgumentException if a part of the string doesn't match a scope name.
     */
    @NonNull
    static Collection<Scope> stringToScopeCollection(@NonNull String scopesString) throws IllegalArgumentException {
        Set<Scope> scopeCollection = new HashSet<>();

        if (scopesString.isEmpty()) {
            return scopeCollection;
        }

        String[] scopeStrings = scopesString.split(" ");
        for (String scopeName : scopeStrings) {
            try {
                scopeCollection.add(Scope.valueOf(scopeName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // do nothing, will omit custom or bad scopes
            }
        }

        return scopeCollection;
    }

    /**
     * Converts a {@link Set} of {@link String}s to {@link Collection} of {@link Scope}s.
     *
     * @param stringSet the {@link Set} of {@link String}s to convert.
     * @return a {@link Collection} of {@link Scope}s.
     */
    @NonNull
    static Collection<Scope> stringCollectionToScopeCollection(@NonNull Collection<String> stringSet)
            throws IllegalArgumentException {
        Set<Scope> scopeCollection = new HashSet<>();

        for (String scopeName : stringSet) {
            scopeCollection.add(Scope.valueOf(scopeName));
        }
        return scopeCollection;
    }

    /**
     * Converts a {@link Collection} of {@link Scope}s into a space-delimited {@link String}.
     *
     * @param scopes the {@link Collection} of {@link Scope}s to convert
     * @return a space-delimited {@link String} of {@link Scope}s
     */
    @NonNull
    public static String scopeCollectionToString(@NonNull Collection<Scope> scopes) {
        Set<String> stringSet = scopeCollectionToStringSet(scopes);
        return TextUtils.join(" ", stringSet).toLowerCase();
    }

    /**
     * Converts a {@link Collection} of {@link String}s into a space-delimited {@link String}.
     *
     * @param scopes the {@link Collection} of {@link String}s to convert
     * @return a space-delimited {@link String} of {@link Scope}s
     */
    public static String customScopeCollectionToString(@NonNull Collection<String> scopes) {
        return TextUtils.join(" ", scopes).toLowerCase();
    }

    /**
     * @param scopes array to return as space delimited
     * @return space-delimited {@link String} of Scopes and Custom Scopes
     */
    public static String mergeScopeStrings(String... scopes) {
        return TextUtils.join(" ", scopes).trim();
    }

    @NonNull
    static Intent parseTokenUri(@NonNull Uri uri) throws LoginAuthenticationException {
        final long expiresIn;
        try {
            expiresIn = Long.valueOf(uri.getQueryParameter(KEY_EXPIRATION_TIME));
        } catch (NumberFormatException ex) {
            throw new LoginAuthenticationException(AuthenticationError.INVALID_RESPONSE);
        }

        final String accessToken = uri.getQueryParameter(KEY_TOKEN);
        final String refreshToken = uri.getQueryParameter(KEY_REFRESH_TOKEN);
        final String scope = uri.getQueryParameter(KEY_SCOPES);
        final String tokenType = uri.getQueryParameter(KEY_TOKEN_TYPE);

        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(scope) || TextUtils.isEmpty(tokenType)) {
            throw new LoginAuthenticationException(AuthenticationError.INVALID_RESPONSE);
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
        data.putExtra(EXTRA_REFRESH_TOKEN, refreshToken);
        data.putExtra(EXTRA_SCOPE, scope);
        data.putExtra(EXTRA_EXPIRES_IN, expiresIn);
        data.putExtra(EXTRA_TOKEN_TYPE, tokenType);
        return data;
    }

    public static String parseAuthorizationCode(@NonNull Uri uri) throws LoginAuthenticationException {
        final String code = uri.getQueryParameter("code");
        if (TextUtils.isEmpty(code)) {
            throw new LoginAuthenticationException(AuthenticationError.INVALID_RESPONSE);
        }

        return code;
    }

    @NonNull
    static AccessToken createAccessToken(Intent intent) {
        String token = intent.getStringExtra(EXTRA_ACCESS_TOKEN);
        String refreshToken = intent.getStringExtra(EXTRA_REFRESH_TOKEN);
        String scope = intent.getStringExtra(EXTRA_SCOPE);
        String tokenType = intent.getStringExtra(EXTRA_TOKEN_TYPE);
        long expiresIn = intent.getLongExtra(EXTRA_EXPIRES_IN, 0);

        return new AccessToken(expiresIn, AuthUtils.stringToScopeCollection
                (scope), token, refreshToken, tokenType);

    }

    public static String createEncodedParam(String rawParam) {
        return Base64.encodeToString(rawParam.getBytes(), Base64.DEFAULT);
    }
}
