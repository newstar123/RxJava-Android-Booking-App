package app.delivering.mvp.main.init.presenter;

import app.core.init.token.interactor.InitAppInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import rx.Observable;

public class MainInitPresenter extends BaseOutputPresenter<Observable<String>> {
    private final InitAppInteractor initAppInteractor;

    public MainInitPresenter(BaseActivity activity) {
        super(activity);
        initAppInteractor = new InitAppInteractor(activity);
    }

    @Override
    public Observable<String> process() {
        return initAppInteractor.process();
    }
}
