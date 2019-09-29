package app.delivering.mvp.ride.order.address.edit.presenter;


import app.core.location.geocode.prediction.entity.PlacePredictionResponse;
import app.core.location.geocode.prediction.interactor.LocationPredictionInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.ride.order.address.edit.model.EditAddressResponse;
import rx.Observable;

public class EditAddressPresenter extends BasePresenter<String, Observable<EditAddressResponse>> {
    private final LocationPredictionInteractor locationPredictionInteractor;

    public EditAddressPresenter(BaseActivity activity) {
        super(activity);
        locationPredictionInteractor = new LocationPredictionInteractor(activity);
    }

    @Override public Observable<EditAddressResponse> process(String s) {
        return locationPredictionInteractor.process(s).map(this::prepare);
    }

    private EditAddressResponse prepare(PlacePredictionResponse result) {
        EditAddressResponse editAddressResponse = new EditAddressResponse();
        editAddressResponse.setResult(result.getResult());
        return editAddressResponse;
    }
}
