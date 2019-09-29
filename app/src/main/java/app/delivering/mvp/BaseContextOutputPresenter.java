package app.delivering.mvp;

import android.content.Context;

public abstract class BaseContextOutputPresenter<OUTPUT> {
    private Context context;

    public BaseContextOutputPresenter(Context context) {
        this.context = context;
    }

    public abstract OUTPUT process();

    public Context getContext() {
        return context;
    }
}
