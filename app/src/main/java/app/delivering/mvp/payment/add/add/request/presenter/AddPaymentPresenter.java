package app.delivering.mvp.payment.add.add.request.presenter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.core.payment.add.entity.AddPaymentModel;
import app.core.payment.add.interactor.AddPaymentInteractor;
import app.core.payment.get.entity.GetPaymentCardsModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.payment.add.add.request.model.AddPaymentBinderRequest;
import rx.Observable;

public class AddPaymentPresenter extends BasePresenter<AddPaymentBinderRequest, Observable<GetPaymentCardsModel>> {
    private final AddPaymentInteractor addPaymentInteractor;

    public AddPaymentPresenter(BaseActivity activity) {
        super(activity);
        addPaymentInteractor = new AddPaymentInteractor(activity);
    }

    @Override public Observable<GetPaymentCardsModel> process(AddPaymentBinderRequest addPaymentBinderRequest) {
        AddPaymentModel addPaymentModel = addPaymentBinderRequest.getAddPaymentModel();
        String expiredMonthYear = addPaymentBinderRequest.getExpiredMonthYear();
        addPaymentModel = addParsed(expiredMonthYear, addPaymentModel);
        return addPaymentInteractor.process(addPaymentModel);
    }

    private AddPaymentModel addParsed(String expiredMonthYear, AddPaymentModel addPaymentModel) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy", Locale.getDefault());
            Date date = simpleDateFormat.parse(expiredMonthYear);
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            addPaymentModel.setExpYear(String.valueOf(instance.get(Calendar.YEAR)));
            addPaymentModel.setExpMonth(String.valueOf(instance.get(Calendar.MONTH) + 1));
            return addPaymentModel;
        } catch (ParseException e) {
            throw new RuntimeException();
        }
    }
}
