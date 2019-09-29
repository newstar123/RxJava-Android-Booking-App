package app.core.payment.regular.interactor;


import app.core.BaseInteractor;
import app.core.payment.regular.gateway.PutRegularPaymentGateway;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.payment.regular.PutRegularPaymentRestGateway;
import rx.Observable;

public class PutRegularPaymentInteractor implements BaseInteractor<String, Observable<String>>{
    private final PermissionInteractor permissionInteractor;
    private final PutRegularPaymentGateway putRegularPaymentGateway;

    public PutRegularPaymentInteractor(BaseActivity activity) {
        permissionInteractor = new PermissionInteractor(activity);
        putRegularPaymentGateway = new PutRegularPaymentRestGateway(activity);
    }

    @Override public Observable<String> process(String s) {
        return permissionInteractor.process()
                .concatMap(isGranted -> putRegularPaymentGateway.put(s));
    }
}
