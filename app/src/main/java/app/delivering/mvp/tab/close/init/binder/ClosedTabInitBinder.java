package app.delivering.mvp.tab.close.init.binder;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.widget.TextView;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.component.tab.close.span.CustomTypefaceSpan;
import app.delivering.mvp.BaseBinder;
import butterknife.BindView;

public class ClosedTabInitBinder extends BaseBinder {
    @BindView(R.id.tab_closed_message) TextView message;
    @BindView(R.id.close_without_uber) TextView buttonNo;

    public ClosedTabInitBinder(BaseActivity activity) {
        super(activity);
    }

    @Override public void afterViewsBounded() {
        String propose = getString(R.string.close_tab_propose);
        Typeface light = ResourcesCompat.getFont(getActivity(), R.font.montserrat_light);
        Typeface bold = ResourcesCompat.getFont(getActivity(), R.font.montserrat_semi_bold);
        SpannableStringBuilder spannable = new SpannableStringBuilder(propose);
        spannable.setSpan(new CustomTypefaceSpan(light), 0, propose.length() - 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan(bold), propose.length() - 5, propose.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), propose.length() - 5, propose.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        message.setText(spannable);
        buttonNo.setText(getString(R.string.close_tab_without_uber));
    }
}
