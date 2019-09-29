package app.delivering.mvp.verify.binder.actionbar;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

import app.R;
import app.delivering.component.verify.VerifyEmailActivity;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class VerifyEmailActionBarBinder extends BaseBinder {
    @BindView(R.id.verify_email_toolbar) Toolbar toolBar;

    public VerifyEmailActionBarBinder(VerifyEmailActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        getActivity().setSupportActionBar(toolBar);
        getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().getSupportActionBar().setHomeButtonEnabled(true);
        getActivity().getSupportActionBar().setTitle("");

        toolBar.setNavigationIcon(R.drawable.inset_white_cross);
        toolBar.setNavigationOnClickListener(click -> {
            closeKeyboard();
            getActivity().onBackPressed();
        });
    }

    private void closeKeyboard() {
        View currentFocusedView = getActivity().getCurrentFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (currentFocusedView != null)
            Objects.requireNonNull(inputManager).
                    hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
