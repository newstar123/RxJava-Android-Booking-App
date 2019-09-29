package app.delivering.mvp.bars.market.init.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import app.core.bars.locations.get.GetBarLocationListInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import rx.Observable;

public class SearchMarketPresenter extends BasePresenter<String, Observable<List<LocationsModel>>> {
    private final GetBarLocationListInteractor locationListInteractor;

    public SearchMarketPresenter(BaseActivity activity) {
        super(activity);
        locationListInteractor = new GetBarLocationListInteractor(activity);
    }

    @Override
    public Observable<List<LocationsModel>> process(String currentMarketName) {
        return locationListInteractor.process()
                .map(locationsModels -> locationsModels.size() > 1 ? getSortedList(locationsModels, currentMarketName) : locationsModels);
    }

    private List<LocationsModel> getSortedList(List<LocationsModel> locationsModels, String currentMarketName) {
        ArrayList<LocationsModel> otherLocations = new ArrayList<>();
        LocationsModel currentLocation = null;
        for (LocationsModel model : locationsModels){
            if (model.getLabel().equalsIgnoreCase(currentMarketName))
                currentLocation = model;
            else
                otherLocations.add(model);
        }

        Collections.sort(otherLocations, (model1, model2) -> model1.getLabel().compareToIgnoreCase(model2.getLabel()));

        ArrayList<LocationsModel> sortedLocations = new ArrayList<>();
        if (currentLocation != null)
            sortedLocations.add(currentLocation);
        sortedLocations.addAll(otherLocations);
        return sortedLocations;
    }
}
