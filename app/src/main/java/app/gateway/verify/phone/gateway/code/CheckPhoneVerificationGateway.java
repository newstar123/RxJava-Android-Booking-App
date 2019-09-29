package app.gateway.verify.phone.gateway.code;

import app.gateway.verify.phone.entity.code.CheckPhoneRequestVerification;
import app.gateway.verify.phone.entity.code.CheckPhoneResponseVerification;
import rx.Observable;

public interface CheckPhoneVerificationGateway {
    public Observable<CheckPhoneResponseVerification> check(CheckPhoneRequestVerification checkPhoneRequestVerification);
}
