package app.delivering.mvp.payment.add.edit.number.binder.handler;


import android.text.Editable;

import java.util.List;

import app.delivering.mvp.payment.add.edit.number.binder.validator.CardType;
import rx.Observable;

public class ChangeCardTypeHandler {
    public static final int PREVIOUS_INDEX = 0;
    public static final int CURRENT_INDEX = 1;

    public Observable<CardType> transform(Editable editable) {
        return Observable.just(editable)
                .map(CharSequence::toString)
                .map(s -> s.replaceAll("\\s+", ""))
                .map(CardType::forCardNumber)
                .buffer(2, 1)
                .filter(cardTypes -> cardTypes.size() == 1
                        || (cardTypes.get(PREVIOUS_INDEX) != cardTypes.get(CURRENT_INDEX)))
                .map(this::getCurrent);
    }

    private CardType getCurrent(List<CardType> cardTypes) {
        if (cardTypes.size() == 1)
            return cardTypes.get(0);
        return cardTypes.get(CURRENT_INDEX);
    }

}
