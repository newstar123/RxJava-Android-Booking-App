package app.delivering.mvp.profile.edit.actionbar.clicks.presenters;

import app.core.login.facebook.entity.LoginResponse;
import app.core.profile.put.entity.PutProfileModel;
import app.core.profile.put.interactor.PutProfileInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class PutUpdatedProfilePresenter extends BasePresenter<PutProfileModel, Observable<LoginResponse>> {
    private final PutProfileInteractor putProfileInteractor;

    public PutUpdatedProfilePresenter(BaseActivity activity) {
        super(activity);
        putProfileInteractor = new PutProfileInteractor(activity);
    }

    @Override public Observable<LoginResponse> process(PutProfileModel profileModel) {
        return putProfileInteractor.process(profileModel);
    }

}
