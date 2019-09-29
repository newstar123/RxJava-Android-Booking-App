package app.delivering.component.network;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;

import app.R;

public class NoInternetConnectionDialog extends Dialog {

    public NoInternetConnectionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_no_connection);
        setCancelable(false);
    }
}
