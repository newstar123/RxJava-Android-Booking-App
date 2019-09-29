package app.delivering.mvp.payment.list.delete.presenter;


import app.core.payment.delete.interactor.DeletePaymentInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.payment.list.delete.model.PaymentsDeleteModel;
import app.delivering.mvp.payment.list.delete.model.PaymentsErrorDeleteModel;
import app.delivering.mvp.payment.list.init.model.PaymentsInitBinderModel;
import rx.Observable;

public class PaymentsDeletePresenter extends BasePresenter<PaymentsDeleteModel, Observable<PaymentsErrorDeleteModel>> {
    private final DeletePaymentInteractor deletePaymentInteractor;

    public PaymentsDeletePresenter(BaseActivity activity) {
        super(activity);
        deletePaymentInteractor = new DeletePaymentInteractor(getActivity());
    }

    @Override public Observable<PaymentsErrorDeleteModel> process(PaymentsDeleteModel model) {
        PaymentsInitBinderModel paymentsInitBinderModel = model.getPaymentsInitBinderModel();
        String cardId = paymentsInitBinderModel.getCardId();
        return deletePaymentInteractor.process(cardId)
                .map(this::createSuccessResponse)
                .onErrorReturn(e -> createErrorResponse(e, model));
    }

    private PaymentsErrorDeleteModel createSuccessResponse(String r) {
        PaymentsErrorDeleteModel paymentsErrorDeleteModel = new PaymentsErrorDeleteModel();
        paymentsErrorDeleteModel.setHasError(false);
        paymentsErrorDeleteModel.setDefaultCard(r);
        return paymentsErrorDeleteModel;
    }

    private PaymentsErrorDeleteModel createErrorResponse(Throwable throwable, PaymentsDeleteModel model) {
        PaymentsErrorDeleteModel paymentsErrorDeleteModel = new PaymentsErrorDeleteModel();
        paymentsErrorDeleteModel.setEvent(model);
        paymentsErrorDeleteModel.setThrowable(throwable);
        paymentsErrorDeleteModel.setHasError(true);
        return paymentsErrorDeleteModel;
    }
}
