package app.core.payment.delete.interactor;


import app.core.BaseInteractor;
import app.core.payment.delete.gateway.DeletePaymentGateway;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.interactor.GetPaymentInteractor;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.payment.delete.DeletePaymentRestGateway;
import rx.Observable;

public class DeletePaymentInteractor implements BaseInteractor<String, Observable<String>> {
    private final GetPaymentInteractor getPaymentsGateway;
    private DeletePaymentGateway deletePaymentGateway;
    private PermissionInteractor permissionInteractor;

    public DeletePaymentInteractor(BaseActivity activity) {
        deletePaymentGateway = new DeletePaymentRestGateway(activity);
        permissionInteractor = new PermissionInteractor(activity);
        getPaymentsGateway = new GetPaymentInteractor(activity);
    }

    @Override public Observable<String> process(String s) {
        return permissionInteractor.process()
                .concatMap(isGranted -> deletePaymentGateway.delete(s))
                .concatMap(r -> getPaymentsGateway.process())
                .map(GetPaymentCardsModel::getDefaultCard);
    }
}
