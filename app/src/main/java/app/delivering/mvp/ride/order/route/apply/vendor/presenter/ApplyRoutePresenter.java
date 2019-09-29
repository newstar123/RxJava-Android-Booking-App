package app.delivering.mvp.ride.order.route.apply.vendor.presenter;


import app.core.location.route.entity.RoutePlaceByIdToBarRequest;
import app.core.location.route.entity.RoutePlaceByIdToBarResponse;
import app.core.location.route.interactor.RoutePlaceByIdToBarInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class ApplyRoutePresenter extends BasePresenter<RoutePlaceByIdToBarRequest, Observable<RoutePlaceByIdToBarResponse>>{
    private final RoutePlaceByIdToBarInteractor interactor;

    public ApplyRoutePresenter(BaseActivity activity) {
        super(activity);
        interactor = new RoutePlaceByIdToBarInteractor(activity);
    }

    @Override public Observable<RoutePlaceByIdToBarResponse> process(RoutePlaceByIdToBarRequest request) {
        return interactor.process(request);
    }
}
