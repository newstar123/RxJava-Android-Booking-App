package app.core.verify.interactor.number;

import app.core.BaseInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.verify.phone.entity.number.StartPhoneRequestVerification;
import app.gateway.verify.phone.entity.number.StartPhoneResponseVerification;
import app.gateway.verify.phone.gateway.number.StartPhoneVerificationRestGateway;
import rx.Observable;

public class VerifyPhoneNumberInteractor implements
        BaseInteractor<StartPhoneRequestVerification, Observable<StartPhoneResponseVerification>> {

    private final CheckNetworkPermissionGateway networkPermissionGateway;
    private final StartPhoneVerificationRestGateway startPhoneVerificationRestGateway;

    public VerifyPhoneNumberInteractor(BaseActivity baseActivity) {
        networkPermissionGateway = new CheckNetworkPermissionGateway(baseActivity);
        startPhoneVerificationRestGateway = new StartPhoneVerificationRestGateway(baseActivity);
    }

    @Override
    public Observable<StartPhoneResponseVerification> process(StartPhoneRequestVerification startPhoneRequestVerification) {
        return networkPermissionGateway.check()
                .concatMap(verify -> startPhoneVerificationRestGateway.verify(startPhoneRequestVerification));
    }
}
