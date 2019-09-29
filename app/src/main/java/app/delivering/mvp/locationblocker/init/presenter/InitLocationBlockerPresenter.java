package app.delivering.mvp.locationblocker.init.presenter;


import app.core.location.blocker.entity.LocationsDemandResponse;
import app.core.location.blocker.interactor.LocationDemandInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class InitLocationBlockerPresenter extends BasePresenter<String, Observable<LocationsDemandResponse>> {

    private final LocationDemandInteractor locationDemandInteractor;


    public InitLocationBlockerPresenter(BaseActivity activity) {
        super(activity);
        locationDemandInteractor = new LocationDemandInteractor(activity);
    }

    @Override
    public Observable<LocationsDemandResponse> process(String email) {
        return locationDemandInteractor.process(email);
    }
}
