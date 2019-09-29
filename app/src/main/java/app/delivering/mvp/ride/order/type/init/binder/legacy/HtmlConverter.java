package app.delivering.mvp.ride.order.type.init.binder.legacy;


import android.text.Html;
import android.text.Spanned;

public class HtmlConverter {
    public static Spanned formatHtmlToSpanned(String string) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        else
            return Html.fromHtml(string);
    }
}
