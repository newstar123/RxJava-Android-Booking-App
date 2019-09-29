package app.core.verify.interactor.code;

import app.core.BaseInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.permissions.network.CheckNetworkPermissionGateway;
import app.gateway.verify.phone.entity.code.CheckPhoneRequestVerification;
import app.gateway.verify.phone.entity.code.CheckPhoneResponseVerification;
import app.gateway.verify.phone.gateway.code.CheckPhoneVerificationGateway;
import app.gateway.verify.phone.gateway.code.CheckPhoneVerificationRestGateway;
import rx.Observable;

public class VerifyCodeInteractor implements
        BaseInteractor<CheckPhoneRequestVerification, Observable<CheckPhoneResponseVerification>> {

    private final CheckNetworkPermissionGateway networkPermissionGateway;
    private final CheckPhoneVerificationGateway checkPhoneVerificationGateway;

    public VerifyCodeInteractor(BaseActivity baseActivity) {
        networkPermissionGateway = new CheckNetworkPermissionGateway(baseActivity);
        checkPhoneVerificationGateway = new CheckPhoneVerificationRestGateway(baseActivity);
    }

    @Override
    public Observable<CheckPhoneResponseVerification> process(CheckPhoneRequestVerification checkPhoneRequestVerification) {
        return networkPermissionGateway.check()
                .concatMap(check -> checkPhoneVerificationGateway.check(checkPhoneRequestVerification));
    }
}
