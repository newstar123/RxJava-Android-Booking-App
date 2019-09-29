package app.delivering.mvp;

import app.delivering.component.BaseActivity;

public abstract class BaseOutputPresenter<OUTPUT> {
    private BaseActivity activity;

    public BaseOutputPresenter(BaseActivity activity) {
        this.activity = activity;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public abstract OUTPUT process();
}
