package app.gateway.verify.phone.gateway.number;


import app.gateway.verify.phone.entity.number.StartPhoneRequestVerification;
import app.gateway.verify.phone.entity.number.StartPhoneResponseVerification;
import rx.Observable;

public interface StartPhoneVerificationGateway {
    public Observable<StartPhoneResponseVerification> verify(StartPhoneRequestVerification startPhoneRequestVerification);
}
