package app.core.init.token.entity;

public class Token {
    private final String authToken;

    public Token(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public String toString() {
        return authToken;
    }

}
