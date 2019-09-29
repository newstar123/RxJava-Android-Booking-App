package app.delivering.mvp.main.actionbar.presenter;

import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileSafeGuestModeInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import rx.Observable;

public class GetInitialAccountMarkPresenter extends BaseOutputPresenter<Observable<Boolean>> {
    private final GetProfileSafeGuestModeInteractor safeGuestModeInteractor;

    public GetInitialAccountMarkPresenter(BaseActivity activity) {
        super(activity);
        safeGuestModeInteractor = new GetProfileSafeGuestModeInteractor(activity);
    }

    @Override
    public Observable<Boolean> process() {
        return safeGuestModeInteractor.process()
                .map(ProfileModel::isAccountVerified);
    }
}
