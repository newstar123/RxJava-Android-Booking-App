package app.core.uber.tobar;

import app.core.BaseInteractor;
import app.core.bars.detail.gateway.GetBarItemGateway;
import app.core.bars.list.get.entity.BarModel;
import app.core.location.current.entity.CurrentPlaceResponse;
import app.core.location.current.interactor.CurrentPlaceInteractor;
import app.core.uber.tobar.entity.OrderRideToBarRequest;
import app.core.uber.tobar.entity.OrderRideToBarResponse;
import app.delivering.component.BaseActivity;
import app.gateway.bars.item.GetBarItemRestGateway;
import rx.Observable;


public class OrderRideToBarInteractor implements BaseInteractor<OrderRideToBarRequest,Observable<OrderRideToBarResponse>> {
    private final CurrentPlaceInteractor currentPlaceInteractor;
    private final GetBarItemGateway barItemGateway;

    public OrderRideToBarInteractor(BaseActivity activity) {
        currentPlaceInteractor = new CurrentPlaceInteractor(activity);
        barItemGateway = new GetBarItemRestGateway();
    }

    @Override public Observable<OrderRideToBarResponse> process(OrderRideToBarRequest request) {
        return Observable.zip(currentPlaceInteractor.process(),
                barItemGateway.get(request.getBarId()), this::merge);
    }

    private OrderRideToBarResponse merge(CurrentPlaceResponse currentPlaceResponse, BarModel barModel) {
        OrderRideToBarResponse orderRideToBarResponse = new OrderRideToBarResponse();
        orderRideToBarResponse.setBarModel(barModel);
        orderRideToBarResponse.setPlaces(currentPlaceResponse.getPlaces());
        return orderRideToBarResponse;
    }
}
