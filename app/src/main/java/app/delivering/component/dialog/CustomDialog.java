package app.delivering.component.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import app.R;
import app.delivering.mvp.dialog.binder.CustomDialogBinder;

public class CustomDialog extends Dialog {

    private CustomDialogBinder customDialogBinder;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setUpDialogOptions(inflate());
    }

    private void setUpDialogOptions(View view) {
        setContentView(view);
        setCancelable(true);
    }

    @Override
    public void dismiss() {
        try {
            getBinder().unbind();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        initBinder();
    }

    @SuppressLint("InflateParams")
    private View inflate() {
        LayoutInflater factory = LayoutInflater.from(this.getContext());
        return factory.inflate(R.layout.dialog_custom_design, null);
    }

    private void initBinder() {
        customDialogBinder = new CustomDialogBinder(this);
    }

    public CustomDialogBinder getBinder() {
        if (customDialogBinder == null)
            throw new RuntimeException();
        return customDialogBinder;
    }
}
