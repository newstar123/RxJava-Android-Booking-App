package app.core.payment.add.interactor;


import app.core.BaseInteractor;
import app.core.payment.add.entity.AddPaymentModel;
import app.core.payment.add.entity.AddPaymentTokenModel;
import app.core.payment.add.gateway.AddPaymentGateway;
import app.core.payment.add.gateway.GetPaymentTokenGateway;
import app.core.payment.get.entity.GetPaymentCardModel;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.interactor.GetPaymentInteractor;
import app.core.permission.interactor.PermissionInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.payment.add.AddPaymentRestGateway;
import app.gateway.payment.token.GetStripePaymentTokenGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class AddPaymentInteractor implements BaseInteractor<AddPaymentModel, Observable<GetPaymentCardsModel>> {
    private final PermissionInteractor permissionInteractor;
    private AddPaymentGateway addPaymentGateway;
    private GetPaymentTokenGateway getPaymentTokenGateway;
    private GetPaymentInteractor getPaymentInteractor;

    public AddPaymentInteractor(BaseActivity activity) {
        permissionInteractor = new PermissionInteractor(activity);
        addPaymentGateway = new AddPaymentRestGateway(activity);
        getPaymentTokenGateway = new GetStripePaymentTokenGateway();
        getPaymentInteractor = new GetPaymentInteractor(activity);
    }

    @Override public Observable<GetPaymentCardsModel> process(AddPaymentModel addPaymentModel) {
        return permissionInteractor.process()
                .concatMap(isGranted -> getPaymentTokenGateway.get(addPaymentModel))
                .observeOn(Schedulers.io())
                .concatMap(token -> addPayment(token, addPaymentModel))
                .concatMap(cardModel -> getPaymentInteractor.process());
    }

    private Observable<GetPaymentCardModel> addPayment(String s, AddPaymentModel addPaymentModel) {
        AddPaymentTokenModel addPaymentTokenModel = new AddPaymentTokenModel();
        addPaymentTokenModel.setToken(s);
        addPaymentTokenModel.setZip(addPaymentModel.getZipCode());
        return addPaymentGateway.add(addPaymentTokenModel);
    }
}
