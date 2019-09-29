package app.delivering.mvp.ride.order.type.pool.capacity.change.presenter;


import app.core.uber.fare.entity.PostUberEstimateRequest;
import app.core.uber.fare.interactor.UberFareInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.component.ride.order.OrderRideActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.fare.apply.model.RideType;
import app.delivering.mvp.ride.order.type.init.presenter.subpresenter.PostUberEstimateToRideType;
import rx.Observable;

public class ChangePoolCapacityPresenter extends BasePresenter<PostUberEstimateRequest, Observable<RideType>> {
    private final UberFareInteractor uberFareInteractor;
    private final PostUberEstimateToRideType postUberEstimateToRideType;

    public ChangePoolCapacityPresenter(BaseActivity activity) {
        super(activity);
        uberFareInteractor = new UberFareInteractor(activity);
        double discount = getActivity().getIntent().getDoubleExtra(OrderRideActivity.ORDER_RIDE_DISCOUNT_KEY, 0);
        postUberEstimateToRideType = new PostUberEstimateToRideType(discount);
    }

    @Override public Observable<RideType> process(PostUberEstimateRequest postUberEstimateRequest) {
        return uberFareInteractor.process(postUberEstimateRequest)
                .map(postUberEstimateToRideType::convert);
    }
}
