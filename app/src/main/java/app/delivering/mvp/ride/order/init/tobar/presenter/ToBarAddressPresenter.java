package app.delivering.mvp.ride.order.init.tobar.presenter;


import com.google.android.gms.location.places.Place;

import app.core.uber.tobar.OrderRideToBarInteractor;
import app.core.uber.tobar.entity.OrderRideToBarRequest;
import app.core.uber.tobar.entity.OrderRideToBarResponse;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.address.apply.standard.events.ApplyAddressEvent;
import app.delivering.mvp.ride.order.init.tobar.model.ToBarAddressModel;
import rx.Observable;

public class ToBarAddressPresenter extends BasePresenter<Long, Observable<ToBarAddressModel>> {
    private final OrderRideToBarInteractor orderRideToBarInteractor;

    public ToBarAddressPresenter(BaseActivity activity) {
        super(activity);
        orderRideToBarInteractor = new OrderRideToBarInteractor(activity);
    }

    @Override public Observable<ToBarAddressModel> process(Long barId) {
        OrderRideToBarRequest request = new OrderRideToBarRequest();
        request.setBarId(barId);
        return orderRideToBarInteractor.process(request).map(this::convert);
    }

    private ToBarAddressModel convert(OrderRideToBarResponse response) {
        Place place = response.getPlaces().get(0);
        ApplyAddressEvent applyAddressEvent = new ApplyAddressEvent(place);
        ToBarAddressModel toBarAddressModel = new ToBarAddressModel();
        toBarAddressModel.setApplyAddressEvent(applyAddressEvent);
        toBarAddressModel.setBarModel(response.getBarModel());
        return toBarAddressModel;

    }
}
