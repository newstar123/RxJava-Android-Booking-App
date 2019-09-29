
package app.core.login.facebook.entity;

public class LoginResponse {
    public static final String GUEST_PASSWORD = "";
    public static final String GUEST_TOKEN = "GUEST_TOKEN";
    private Integer id;
    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
