package app.delivering.mvp.payment.list.init.presenter;


import android.view.View;

import java.util.List;

import app.R;
import app.core.payment.get.entity.GetPaymentCardModel;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.core.payment.get.interactor.GetPaymentInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.payment.list.adapter.PaymentCardImage;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.payment.list.init.model.PaymentsInitBinderModel;
import rx.Observable;

public class PaymentsInitPresenter extends BaseOutputPresenter<Observable<List<PaymentsInitBinderModel>>> {
    private final GetPaymentInteractor getPaymentInteractor;

    public PaymentsInitPresenter(BaseActivity activity) {
        super(activity);
        getPaymentInteractor = new GetPaymentInteractor(activity);
    }

    @Override public Observable<List<PaymentsInitBinderModel>> process() {
        return getPaymentInteractor.process().map(this::prepare);
    }

    private List<PaymentsInitBinderModel> prepare(GetPaymentCardsModel getPaymentCardsModel) {
        String defaultCardId = getPaymentCardsModel.getDefaultCard();
        return Observable.from(getPaymentCardsModel.getCards())
                .map(cardModel -> prepareItem(cardModel, defaultCardId))
                .toList()
                .toBlocking()
                .single();
    }

    private PaymentsInitBinderModel prepareItem(GetPaymentCardModel getPaymentCardModel, String defaultCardId) {
        PaymentsInitBinderModel paymentsInitBinderModel = new PaymentsInitBinderModel();
        if (getPaymentCardModel.getId().equals(defaultCardId))
            paymentsInitBinderModel.setDefaultCard(View.VISIBLE);
        else
            paymentsInitBinderModel.setDefaultCard(View.GONE);
        String brandString = getPaymentCardModel.getBrand();
        int brandDrawableId = PaymentCardImage.get(brandString);
        paymentsInitBinderModel.setBrand(brandDrawableId);
        String hiddenCardTemplate = getActivity().getString(R.string.masked_credit_card);
        String hiddenCardNumber = String.format(hiddenCardTemplate, getPaymentCardModel.getLast4());
        paymentsInitBinderModel.setHiddenCardNumber(hiddenCardNumber);
        paymentsInitBinderModel.setCardId(getPaymentCardModel.getId());
        return paymentsInitBinderModel;
    }


}
