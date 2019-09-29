package app.qamode.mvp.validator;

import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import app.R;
import app.delivering.component.BaseActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class EditValidator {
    public static final String REGULAR_LETTER = "^[\\p{L}\\p{Nd}\\s_-]*$";
    BaseActivity activity;

    public EditValidator(BaseActivity activity) {
        this.activity = activity;
    }

    public Observable<Boolean> createSimpleValidator(EditText editText, TextInputLayout inputLayout) {
        return createEditTextObservable(editText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(s -> validate(s, inputLayout));
    }

    public Boolean validate(String string, TextInputLayout name) {
        try {
            throwFieldEmpty(string);
            restoreErrorState(name);
           // throwNotValidLetter(string);
            return true;
        } catch (ValidateException e) {
            showFieldError(name, e.getErrorMessage());
            return false;
        }
    }

    public Observable<Boolean> createEmailObservable(EditText email, TextInputLayout inputLayout) {
        return createEditTextObservable(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(s -> createValidUserObservable(s, inputLayout));
    }

    public Boolean createValidUserObservable(String email, TextInputLayout editText) {
        try {
            throwFieldEmpty(email);
            throwEmailInvalid(email);
            restoreErrorState(editText);
            return true;
        } catch (ValidateException e) {
            showFieldError(editText, e.getErrorMessage());
            return false;
        }
    }

    private Observable<String> createEditTextObservable(EditText editText) {
        return RxTextView.textChanges(editText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .skip(1)
                .map(charSequence -> charSequence.toString().trim());
    }

    private void showFieldError(TextInputLayout inputLayout, String message) {
        inputLayout.setError(message);
        inputLayout.setErrorEnabled(true);
        inputLayout.requestFocus();
    }

    private void restoreErrorState(TextInputLayout inputLayout) {
        inputLayout.setError("");
        inputLayout.setErrorEnabled(false);
        inputLayout.requestFocus();
    }

    private void throwFieldEmpty(String s) throws ValidateException {
        String message = activity.getString(R.string.should_not_empty);
        if (TextUtils.isEmpty(s)) throw new ValidateException(message);
    }

    private void throwEmailInvalid(String s) throws ValidateException {
        String message = activity.getString(R.string.error_bad_email);
        if (!Patterns.EMAIL_ADDRESS.matcher(s).matches())
            throw new ValidateException(message);
    }

    private void throwNotValidLetter(String s) throws ValidateException {
        String message = activity.getString(R.string.letters_and_digits);
        if (!s.matches(REGULAR_LETTER)) throw new ValidateException(message);
    }

}
