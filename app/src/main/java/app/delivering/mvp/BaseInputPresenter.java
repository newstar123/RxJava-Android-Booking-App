package app.delivering.mvp;

import app.delivering.component.BaseActivity;

public abstract class BaseInputPresenter<INPUT> {
    private BaseActivity activity;

    public BaseInputPresenter(BaseActivity activity) {
        this.activity = activity;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public abstract void process(INPUT input);
}
