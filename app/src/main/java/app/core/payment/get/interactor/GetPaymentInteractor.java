package app.core.payment.get.interactor;


import app.core.BaseOutputInteractor;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.gateway.GetPaymentsGateway;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.payment.get.GetPaymentsRestGateway;
import rx.Observable;

public class GetPaymentInteractor implements BaseOutputInteractor<Observable<GetPaymentCardsModel>>{
    private final PermissionInteractor permissionInteractor;
    private GetPaymentsGateway getPaymentsGateway;

    public GetPaymentInteractor(BaseActivity activity) {
        permissionInteractor = new PermissionInteractor(activity);
        getPaymentsGateway = new GetPaymentsRestGateway(activity);
    }

    @Override public Observable<GetPaymentCardsModel> process() {
        return permissionInteractor.process()
                .concatMap(isGranted -> getPaymentsGateway.get());
    }
}
