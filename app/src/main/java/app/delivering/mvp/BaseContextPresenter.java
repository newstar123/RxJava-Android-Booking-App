package app.delivering.mvp;

import android.content.Context;

public abstract class BaseContextPresenter<INPUT,OUTPUT> {
    private Context context;

    public BaseContextPresenter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract OUTPUT process(INPUT input);
}
