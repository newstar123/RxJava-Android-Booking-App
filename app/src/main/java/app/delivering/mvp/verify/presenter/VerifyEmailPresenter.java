package app.delivering.mvp.verify.presenter;

import app.core.login.facebook.entity.LoginResponse;
import app.core.profile.put.entity.PutProfileModel;
import app.core.verify.interactor.mail.VerifyEmailInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class VerifyEmailPresenter extends BasePresenter<PutProfileModel, Observable<LoginResponse>> {

    private VerifyEmailInteractor verifyEmailInteractor;

    public VerifyEmailPresenter(BaseActivity activity) {
        super(activity);
        verifyEmailInteractor = new VerifyEmailInteractor(activity);
    }

    @Override
    public Observable<LoginResponse> process(PutProfileModel putProfileModel) {
        return verifyEmailInteractor.process(putProfileModel);
    }
}
