package app.core.payment.regular.model;

public class RegularPaymentRequest {
    private String id;

    public RegularPaymentRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
