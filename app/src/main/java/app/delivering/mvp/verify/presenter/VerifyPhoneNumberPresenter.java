package app.delivering.mvp.verify.presenter;

import app.core.verify.interactor.number.VerifyPhoneNumberInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.gateway.verify.phone.entity.number.StartPhoneRequestVerification;
import app.gateway.verify.phone.entity.number.StartPhoneResponseVerification;
import rx.Observable;

public class VerifyPhoneNumberPresenter extends
        BasePresenter<StartPhoneRequestVerification, Observable<StartPhoneResponseVerification>> {

    private VerifyPhoneNumberInteractor verifyPhoneNumberInteractor;

    public VerifyPhoneNumberPresenter(BaseActivity activity) {
        super(activity);
        verifyPhoneNumberInteractor = new VerifyPhoneNumberInteractor(activity);
    }

    @Override
    public Observable<StartPhoneResponseVerification> process(StartPhoneRequestVerification startPhoneRequestVerification) {
        return verifyPhoneNumberInteractor.process(startPhoneRequestVerification);
    }
}
