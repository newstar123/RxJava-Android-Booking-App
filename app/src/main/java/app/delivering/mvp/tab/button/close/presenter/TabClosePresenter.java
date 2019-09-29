package app.delivering.mvp.tab.button.close.presenter;

import app.core.checkin.user.post.entity.CheckInResponse;
import app.core.checkout.entity.CheckOutRequestBody;
import app.core.checkout.interactor.CheckoutInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class TabClosePresenter extends BasePresenter<CheckOutRequestBody, Observable<CheckInResponse>> {
    private CheckoutInteractor checkoutInteractor;

    public TabClosePresenter(BaseActivity activity) {
        super(activity);
        checkoutInteractor = new CheckoutInteractor(getActivity());
    }

    @Override public Observable<CheckInResponse> process(CheckOutRequestBody requestBody) {
        return checkoutInteractor.process(requestBody);
    }
}
