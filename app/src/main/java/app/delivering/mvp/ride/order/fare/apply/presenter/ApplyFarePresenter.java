package app.delivering.mvp.ride.order.fare.apply.presenter;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import app.core.uber.fares.entity.PostUberEstimatesRequest;
import app.core.uber.fares.interactor.UberFaresInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.fare.apply.events.ApplyFareEvent;
import app.delivering.mvp.ride.order.fare.apply.model.RideCategory;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.SortRideEstimatesByCategories;
import rx.Observable;

public class ApplyFarePresenter extends BasePresenter<ApplyFareEvent, Observable<List<RideCategory>>> {
    private final UberFaresInteractor uberFaresInteractor;
    private final SortRideEstimatesByCategories sortRideEstimatesByCategories;

    public ApplyFarePresenter(BaseActivity activity) {
        super(activity);
        uberFaresInteractor = new UberFaresInteractor(activity);
        double discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        sortRideEstimatesByCategories = new SortRideEstimatesByCategories(discount);
    }

    @Override public Observable<List<RideCategory>> process(ApplyFareEvent applyFareEvent) {
        PostUberEstimatesRequest postUberEstimatesRequest = new PostUberEstimatesRequest();
        postUberEstimatesRequest.setProducts(applyFareEvent.getProducts());
        List<LatLng> route = applyFareEvent.getRoute();
        postUberEstimatesRequest.setDeparture(route.get(0));
        postUberEstimatesRequest.setDestination(route.get(route.size() - 1));
        return uberFaresInteractor.process(postUberEstimatesRequest)
                .map(result -> sortRideEstimatesByCategories.prepareRides(result.getEstimates()));
    }
}
