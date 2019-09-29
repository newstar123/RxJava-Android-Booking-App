package app.delivering.mvp;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.dialog.RxDialogHandler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public abstract class BaseBinder {
    private BaseActivity activity;
    private View progress;
    private boolean wasProgressState;

    public BaseBinder(BaseActivity activity) {
        this.activity = activity;
    }

    protected BaseActivity getActivity() {
        return activity;
    }

    public void afterViewsBounded() {

    }

    protected View findViewById(@IdRes int id) {
        return getActivity().findViewById(id);
    }

    protected String getString(@StringRes int id) {
        return getActivity().getString(id);
    }

    protected void showProgress() {
        wasProgressState = true;
        if (progress == null)
            getActivity().findViewById(R.id.progress).setVisibility(View.VISIBLE);
        else
            progress.setVisibility(View.VISIBLE);
    }

    protected void hideProgress() {
        wasProgressState = false;
        if (progress == null)
            getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
        else
            progress.setVisibility(View.GONE);
    }

    protected void showDialogMessage(@StringRes int stringId) {
        new RxDialogHandler(getActivity()).showOneButtonWithoutTitle(getString(stringId), R.string.ok)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(click -> {
                }, e -> {
                }, () -> {
                });
    }

    protected Spanned formatHtmlToSpanned(@StringRes int stringId) {
        String string = getString(stringId);
        return formatHtmlToSpanned(string);
    }

    protected Spanned formatHtmlToSpanned(String string) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        else
            return Html.fromHtml(string);
    }

    protected String getUnspacedString(EditText editText) {
        return getString(editText).replaceAll("\\s+", "");
    }

    protected String getString(EditText editText) {
        return editText.getText().toString();
    }

    protected boolean hasToRestore(Subscription binderSubscribe) {
        return binderSubscribe != null && !binderSubscribe.isUnsubscribed();
    }

    protected boolean hasToRestore() {
        return wasProgressState;
    }

    public void closeSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setProgress(View progress) {
        this.progress = progress;
    }

    protected float getDimension(@DimenRes int dimId) {
        return getActivity().getResources().getDimension(dimId);
    }
}
