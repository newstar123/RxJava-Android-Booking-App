package app.delivering.mvp.ride.order.type.init.presenter;


import app.core.uber.cache.get.interactor.GetUberRideEstimatesCacheInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.SortRideEstimatesByCategories;
import rx.Observable;

public class OrderRideTypeInitPresenter extends BasePresenter<String, Observable<RideCategory>> {
    private final GetUberRideEstimatesCacheInteractor interactor;
    private final SortRideEstimatesByCategories sortRideEstimatesByCategories;

    public OrderRideTypeInitPresenter(BaseActivity activity) {
        super(activity);
        interactor = new GetUberRideEstimatesCacheInteractor();
        double discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        sortRideEstimatesByCategories = new SortRideEstimatesByCategories(discount);
    }

    @Override public Observable<RideCategory> process(String s) {
        return interactor.process()
                .map(sortRideEstimatesByCategories::prepareRides)
                .flatMapIterable(items -> items)
                .filter(category -> category.getName().equals(s))
                .first();
    }
}
