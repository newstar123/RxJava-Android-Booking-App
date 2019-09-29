package app.delivering.mvp.bars.market.actionbar.presenter;

import app.core.profile.get.entity.ProfileModel;
import app.core.profile.get.interactor.GetProfileSafeGuestModeInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import rx.Observable;

public class GetAccountMarkPresenter extends BaseOutputPresenter<Observable<Boolean>> {
    private final GetProfileSafeGuestModeInteractor safeGuestModeInteractor;

    public GetAccountMarkPresenter(BaseActivity activity) {
        super(activity);
        safeGuestModeInteractor = new GetProfileSafeGuestModeInteractor(activity);
    }

    @Override
    public Observable<Boolean> process() {
        return safeGuestModeInteractor.process()
                .map(ProfileModel::isAccountVerified);
    }
}