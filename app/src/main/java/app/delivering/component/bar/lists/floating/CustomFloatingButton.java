package app.delivering.component.bar.lists.floating;

import android.content.Context;
import android.util.AttributeSet;

public class CustomFloatingButton extends android.support.v7.widget.AppCompatButton {
    private int mUserSetVisibility;

    public CustomFloatingButton(Context context) {
        this(context, null);
    }

    public CustomFloatingButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFloatingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mUserSetVisibility = getVisibility();
    }

    @Override
    public void setVisibility(int visibility) {
        internalSetVisibility(visibility, true);
    }

    final void internalSetVisibility(int visibility, boolean fromUser) {
        super.setVisibility(visibility);
        if (fromUser) {
            mUserSetVisibility = visibility;
        }
    }

    final int getUserSetVisibility() {
        return mUserSetVisibility;
    }
}
