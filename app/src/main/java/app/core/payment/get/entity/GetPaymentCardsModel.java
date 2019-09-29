package app.core.payment.get.entity;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPaymentCardsModel {
    private List<GetPaymentCardModel> cards;
    @SerializedName("default") private String defaultCard;

    public List<GetPaymentCardModel> getCards() {
        return cards;
    }

    public String getDefaultCard() {
        return defaultCard;
    }
}
