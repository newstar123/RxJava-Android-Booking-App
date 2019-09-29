package app.core.payment.add.entity;



public class AddPaymentTokenModel {
    private String zip;
    private String token;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
