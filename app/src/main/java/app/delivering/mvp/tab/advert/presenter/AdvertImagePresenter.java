package app.delivering.mvp.tab.advert.presenter;

import app.core.advert.interactor.GetAdvertInteractor;
import app.core.ride.delayed.discount.interactor.GetRideDiscountInteractor;
import app.core.ride.delayed.discount.model.DiscountUpdatesModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class AdvertImagePresenter extends BasePresenter<Double, Observable<DiscountUpdatesModel>> {
    private final GetAdvertInteractor getAdvertInteractor;
    private final GetRideDiscountInteractor getRideDiscountInteractor;
    private final DiscountUpdatesModel discountUpdatesModel;

    public AdvertImagePresenter(BaseActivity activity) {
        super(activity);
        getAdvertInteractor = new GetAdvertInteractor(getActivity());
        getRideDiscountInteractor = new GetRideDiscountInteractor();
        discountUpdatesModel = new DiscountUpdatesModel();
    }

    @Override
    public Observable<DiscountUpdatesModel> process(Double discount) {
        return Observable.zip(getAdvertInteractor.process(), getRideDiscountInteractor.process(),
                (advertResponse, rideDiscountVal) -> {
                    discountUpdatesModel.setAdvertResponse(advertResponse);
                    discountUpdatesModel.setEligible(rideDiscountVal > 0 || discount > 0);
                    discountUpdatesModel.setRideDiscountVal(rideDiscountVal > 0 ? rideDiscountVal : discount);
                    return discountUpdatesModel;
                });
    }
}
