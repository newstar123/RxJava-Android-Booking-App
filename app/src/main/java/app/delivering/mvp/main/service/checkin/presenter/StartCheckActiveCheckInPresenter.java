package app.delivering.mvp.main.service.checkin.presenter;

import app.core.checkin.active.CheckActiveCheckInsInteractor;
import app.core.checkin.user.get.entity.GetCheckInsResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.main.service.checkin.model.CheckActiveCheckInResult;
import rx.Observable;

public class StartCheckActiveCheckInPresenter extends BasePresenter<GetCheckInsResponse, Observable<CheckActiveCheckInResult>> {
    private CheckActiveCheckInsInteractor checkActiveCheckInsInteractor;

    public StartCheckActiveCheckInPresenter(BaseActivity activity) {
        super(activity);
        checkActiveCheckInsInteractor = new CheckActiveCheckInsInteractor(getActivity());
    }

    @Override public Observable<CheckActiveCheckInResult> process(GetCheckInsResponse activeCheckIn) {
        return checkActiveCheckInsInteractor.process(activeCheckIn);
    }

}
