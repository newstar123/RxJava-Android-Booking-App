package app.delivering.mvp.bars.detail.init.call.presenter;

import app.core.permission.callphone.CallPhonePermissionInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseOutputPresenter;
import rx.Observable;

public class BarDetailCallPhonePresenter extends BaseOutputPresenter<Observable<Boolean>> {
    private CallPhonePermissionInteractor interactor;

    public BarDetailCallPhonePresenter(BaseActivity activity) {
        super(activity);
        interactor = new CallPhonePermissionInteractor(getActivity());
    }

    @Override public Observable<Boolean> process() {
        return interactor.process();
    }
}
