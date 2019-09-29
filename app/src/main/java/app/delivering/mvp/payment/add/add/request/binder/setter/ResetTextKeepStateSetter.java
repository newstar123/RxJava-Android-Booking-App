package app.delivering.mvp.payment.add.add.request.binder.setter;

import android.widget.EditText;

import butterknife.ButterKnife;


public class ResetTextKeepStateSetter {
    public static final ButterKnife.Action<EditText> RESET = (view, index) -> view.setTextKeepState(view.getText().toString());
    public static final ButterKnife.Action<EditText> UNFOCUSED = (view, index) -> view.clearFocus();
}
