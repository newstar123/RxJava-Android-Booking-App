package app.delivering.mvp.payment.add.edit.number.binder.handler;


import android.text.InputFilter;
import android.widget.EditText;

public class ChangeInputType {
    public static void changeInputType(EditText editText, int length) {
        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(length);
        InputFilter[] filters = {lengthFilter};
        editText.setFilters(filters);
        editText.invalidate();
    }
}
