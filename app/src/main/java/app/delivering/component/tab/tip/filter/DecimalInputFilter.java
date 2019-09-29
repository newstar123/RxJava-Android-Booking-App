package app.delivering.component.tab.tip.filter;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalInputFilter implements InputFilter {
    private int digitsBeforeDot;
    private int digitsAfterDot;

    public DecimalInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        this.digitsBeforeDot = digitsBeforeZero;
        this.digitsAfterDot = digitsAfterZero;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String temp = dest.toString() + source.toString();

        if (temp.equals("."))
            return "0.";
        else if (!temp.contains(".")) {
            if (temp.length() > digitsBeforeDot)
                return "";
        } else {
            temp = temp.substring(temp.indexOf(".") + 1);
            if (temp.length() > digitsAfterDot)
                return "";
        }
        return null;
    }

}
