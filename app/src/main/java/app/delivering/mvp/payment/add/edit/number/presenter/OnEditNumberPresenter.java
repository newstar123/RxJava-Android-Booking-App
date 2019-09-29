package app.delivering.mvp.payment.add.edit.number.presenter;


import android.text.Editable;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.payment.add.edit.number.binder.validator.CardType;
import rx.Observable;

public class OnEditNumberPresenter extends BasePresenter<Observable<Editable>,Observable<CardType>> {

    public OnEditNumberPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override public Observable<CardType> process(Observable<Editable> editableObservable) {
        return null;
    }
}
