package app.delivering.mvp.main.show.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import app.core.bars.locations.near.GetNearBarLocationInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.main.show.model.CitiesModel;
import rx.Observable;

public class SearchBarByCityPresenter extends BasePresenter<List<LocationsModel>, Observable<CitiesModel>> {
    private GetNearBarLocationInteractor nearBarLocationInteractor;

    public SearchBarByCityPresenter(BaseActivity activity) {
        super(activity);
        nearBarLocationInteractor = new GetNearBarLocationInteractor(getActivity());
    }

    @Override public Observable<CitiesModel> process(List<LocationsModel> models) {
        return nearBarLocationInteractor.process(models)
                .single()
                .concatMap(nearLocation -> getFilledModel(nearLocation, models));
    }

    private Observable<CitiesModel> getFilledModel(LocationsModel nearLocation, List<LocationsModel> locations) {
        return Observable.create(subscriber -> {
            ArrayList<String> names = new ArrayList<String>();
            for (LocationsModel location : locations)
                names.add(location.getLabel());
            Collections.sort(names);
            CitiesModel citiesModel = new CitiesModel();
            citiesModel.setCities(locations);
            citiesModel.setSpinnerNames(names);
            citiesModel.setNearLocation(nearLocation);
            citiesModel.setSelectCityName(nearLocation.getLabel());
            subscriber.onNext(citiesModel);
            subscriber.onCompleted();
        });
    }
}
