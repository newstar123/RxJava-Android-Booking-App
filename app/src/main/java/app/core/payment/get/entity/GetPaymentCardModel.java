package app.core.payment.get.entity;


public class GetPaymentCardModel {
    private String id;
    private int expiryMonth;
    private int expiryYear;
    private String brand;
    private String last4;
    private boolean isDefault;

    public String getId() {
        return id;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public String getBrand() {
        return brand;
    }

    public String getLast4() {
        return last4;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
