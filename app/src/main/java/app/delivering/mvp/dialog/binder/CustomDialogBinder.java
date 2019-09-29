package app.delivering.mvp.dialog.binder;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.R;
import app.delivering.component.dialog.CustomDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CustomDialogBinder {

    @BindView(R.id.root_dialog_layout) LinearLayout rootLayout;
    @BindView(R.id.custom_dialog_title) TextView customTitle;
    @BindView(R.id.custom_dialog_message) TextView customMessage;
    @BindView(R.id.custom_dialog_italic_message) TextView customItalicMessage;
    @BindView(R.id.button_root_layout) RelativeLayout buttonsRootLayout;
    @BindView(R.id.custom_left_button) TextView leftButton;
    @BindView(R.id.custom_right_button) TextView rightButton;

    private Unbinder unbinder;

    public CustomDialogBinder(CustomDialog customDialog) {
        unbinder = ButterKnife.bind(this, customDialog);
    }

    public void setDialogTitle(@StringRes int title) {
        if (title == 0)
            setTitleVisibility(false);
        else {
            setTitleVisibility(true);
            customTitle.setText(title);
        }
    }

    public void setDialogStringTitle(String title) {
        if (TextUtils.isEmpty(title))
            setTitleVisibility(false);
        else {
            setTitleVisibility(true);
            customTitle.setText(title);
        }
    }

    private void setTitleVisibility(boolean isTitleVisible) {
        customTitle.setVisibility(isTitleVisible ? View.VISIBLE : View.GONE);
    }

    public void setDialogMessage(@StringRes int message) {
        customMessage.setText(message);
    }

    public void setDialogStringMessage(String message) {
        customMessage.setText(message);
    }

    public TextView leftButton(@StringRes int res) {
        leftButton.setVisibility((res == 0) ? View.GONE : View.VISIBLE);
        if (res != 0)
            leftButton.setText(res);
        return leftButton;
    }

    public TextView rightButton(@StringRes int res) {
        rightButton.setText(res);
        return rightButton;
    }

    public void unbind() {
        unbinder.unbind();
    }
}
