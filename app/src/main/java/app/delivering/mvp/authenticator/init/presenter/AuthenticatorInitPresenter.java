package app.delivering.mvp.authenticator.init.presenter;


import app.core.init.notoken.interactor.InitNoTokenInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import rx.Observable;

public class AuthenticatorInitPresenter extends BaseOutputPresenter<Observable<String>> {
    private final InitNoTokenInteractor initNoTokenInteractor;

    public AuthenticatorInitPresenter(BaseActivity activity) {
        super(activity);
        initNoTokenInteractor = new InitNoTokenInteractor(activity);
    }

    @Override public Observable<String> process() {
        return initNoTokenInteractor.process();
    }
}
