package app.delivering.component.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import app.R;

public class CustomUpdateDialog extends Dialog {
    private TextView message;

    public CustomUpdateDialog(Context context) {
        super(context);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_design);
        message = findViewById(R.id.custom_update_dialog_message);
        setCancelable(false);
    }

    public void setMessage(String messageText) {
        message.setText(messageText);
    }
}