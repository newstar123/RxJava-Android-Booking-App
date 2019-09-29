package app.delivering.mvp.payment.add.edit.number.binder.handler;


import android.text.Editable;
import android.text.Spanned;

import app.delivering.mvp.payment.add.edit.number.binder.validator.AppendSpaceSpan;
import app.delivering.mvp.payment.add.edit.number.binder.validator.CardType;
import rx.Observable;

public class AddCardSpansHandler {

    public Observable<Editable> transform(Editable editableObservable) {
        return Observable.just(editableObservable).map(this::addSpans);
    }

    private Editable addSpans(Editable editable) {
        CardType cardType = CardType.forCardNumber(editable.toString());
        int[] spaceIndices = cardType.getSpaceIndices();
        final int length = editable.length();
        for (int index : spaceIndices) {
            if (index <= length) {
                editable.setSpan(new AppendSpaceSpan(), index - 1, index,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return editable;
    }
}
