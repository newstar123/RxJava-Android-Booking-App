package app.delivering.mvp.payment.add.edit.number.binder.validator;


import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.trello.rxlifecycle.android.ActivityEvent;

import app.delivering.component.BaseActivity;
import rx.Observable;

public class RxBindingValidator {
    public static Observable<String> create(EditText editText, BaseActivity activity) {
        return RxTextView.afterTextChangeEvents(editText)
                .skip(1)
                .map(TextViewAfterTextChangeEvent::editable)
                .map(CharSequence::toString)
                .compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }
}
