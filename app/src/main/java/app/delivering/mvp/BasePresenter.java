package app.delivering.mvp;

import android.text.Html;
import android.text.Spanned;

import app.delivering.component.BaseActivity;

public abstract class BasePresenter<INPUT,OUTPUT> {
    private BaseActivity activity;

    public BasePresenter(BaseActivity activity) {
        this.activity = activity;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public abstract OUTPUT process(INPUT input);

    protected Spanned formatHtmlToSpanned(String string) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        else
            return Html.fromHtml(string);
    }
}
