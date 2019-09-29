package app.gateway.ride.token.register;


import com.google.gson.annotations.SerializedName;

public class RegisterRideAuthResponseBody {
    private String scope;
    @SerializedName("access_token") private String accessToken;
    @SerializedName("expires_in") private String expiresIn;
    @SerializedName("token_type") private String tokenType;
    @SerializedName("refresh_token") private String refreshToken;

    public String getScope() {
        return scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
