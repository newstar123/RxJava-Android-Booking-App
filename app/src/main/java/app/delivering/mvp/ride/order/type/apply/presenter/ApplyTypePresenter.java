package app.delivering.mvp.ride.order.type.apply.presenter;


import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import app.core.uber.product.entity.UberProductRequest;
import app.core.uber.product.interactor.UberProductsInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.type.apply.model.ApplyTypesModel;
import app.delivering.mvp.ride.order.type.apply.presenter.subpresenter.SortProductsByCategories;
import rx.Observable;

public class ApplyTypePresenter extends BasePresenter<List<LatLng>, Observable<ApplyTypesModel>> {
    private final UberProductsInteractor uberProductsInteractor;
    private final SortProductsByCategories sortProductsByCategories;

    public ApplyTypePresenter(BaseActivity activity) {
        super(activity);
        uberProductsInteractor = new UberProductsInteractor(activity);
        sortProductsByCategories = new SortProductsByCategories();
    }

    @Override public Observable<ApplyTypesModel> process(List<LatLng> latLngs) {
        UberProductRequest uberProductRequest = new UberProductRequest();
        uberProductRequest.setDeparture(latLngs.get(0));
        return uberProductsInteractor.process(uberProductRequest)
                .map(sortProductsByCategories::prepareRides)
                .doOnNext(model -> model.setRoute(latLngs));
    }
}
