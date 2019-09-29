package app.delivering.mvp.payment.list.init.model;

import android.support.annotation.DrawableRes;

public class PaymentsInitBinderModel {
    @DrawableRes
    private int brand;
    private String hiddenCardNumber;
    private int defaultCard;
    private String cardId;

    public int getBrand() {
        return brand;
    }

    public void setBrand(int brand) {
        this.brand = brand;
    }

    public String getHiddenCardNumber() {
        return hiddenCardNumber;
    }

    public void setHiddenCardNumber(String last4) {
        this.hiddenCardNumber = last4;
    }

    public int getDefaultCard() {
        return defaultCard;
    }

    public void setDefaultCard(int defaultCard) {
        this.defaultCard = defaultCard;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

}
