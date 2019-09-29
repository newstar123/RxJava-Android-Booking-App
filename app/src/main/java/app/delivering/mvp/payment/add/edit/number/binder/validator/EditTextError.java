package app.delivering.mvp.payment.add.edit.number.binder.validator;


import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;

public class EditTextError {
    private TextInputLayout textInputLayout;

    public EditTextError(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public boolean reset() {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    public boolean show(@StringRes int stringId) {
        String string = textInputLayout.getResources().getString(stringId);
        textInputLayout.setError(string);
        textInputLayout.setErrorEnabled(true);
        return false;
    }
}
