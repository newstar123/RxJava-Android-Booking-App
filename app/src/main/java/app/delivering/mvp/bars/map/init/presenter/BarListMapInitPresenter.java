package app.delivering.mvp.bars.map.init.presenter;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.R;
import app.core.bars.list.get.entity.BarListModel;
import app.core.bars.list.get.entity.BarModel;
import app.core.bars.list.get.interactor.GetBarListInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.delivering.mvp.bars.work.BarWorkTypeParser;
import app.delivering.mvp.bars.work.model.BarWorkTypeModel;
import rx.Observable;

public class BarListMapInitPresenter extends BasePresenter<CitiesModel, Observable<List<BarListModel>>> {

    private final GetBarListInteractor barListInteractor;

    public BarListMapInitPresenter(BaseActivity activity) {
        super(activity);
        barListInteractor = new GetBarListInteractor(activity);
    }

    @Override
    public Observable<List<BarListModel>> process(CitiesModel citiesModel) {
        return barListInteractor.process()
                .map(barModels -> TextUtils.isEmpty(citiesModel.getSelectCityName()) ? getFullList(barModels)
                        : getListByCityName(barModels, citiesModel.getSelectCityName()))
                .single()
                .map(barListModels -> barListModels);
    }


    private ArrayList<BarListModel> getFullList(List<BarModel> barModels) {
        ArrayList<BarListModel> listModels = new ArrayList<>();
        for (BarModel barModel : barModels) {
            BarListModel listModel = createListModel(barModel);
            listModels.add(listModel);
        }
        return listModels;
    }

    private ArrayList<BarListModel> getListByCityName(List<BarModel> barModels, String selectCityName) {
        ArrayList<BarListModel> listModels = new ArrayList<>();
        for (BarModel barModel : barModels) {
            if (barModel.getLocation().getLabel().equals(selectCityName)) {
                BarListModel listModel = createListModel(barModel);
                listModels.add(listModel);
            }
        }
        return listModels;
    }

    private BarListModel createListModel(BarModel barModel) {
        BarListModel listModel = new BarListModel();
        listModel.setId(barModel.getId());
        listModel.setName(barModel.getName());

        listModel.setCity(barModel.getCity());
        listModel.setDistKm(barModel.getDistKm());
        listModel.setDistMiles(barModel.getDistMiles());
        listModel.setNeighborhood(barModel.getNeighborhood());
        listModel.setLatitude(barModel.getLatitude());
        listModel.setLongitude(barModel.getLongitude());
        listModel.setRouting(getFormatRouting(barModel.getDistMiles(), barModel.getNeighborhood()));
        listModel.setDiscountText(getFormatDiscount(barModel.getCurrentDiscount()));
        trySetWorkType(listModel, barModel);

        return listModel;
    }

    private void trySetWorkType(BarListModel listModel, BarModel barModel) {
        try {
            BarWorkTypeModel workTypeModel = BarWorkTypeParser.createWorkTypeInformation(barModel);
            listModel.setBarWorkTimeType(workTypeModel.getBarWorkTimeType());
            listModel.setWorkTypeText(workTypeModel.getWorkTypeText());
        } catch (Exception e) {
            listModel.setBarWorkTimeType(BarByWorkTime.OPEN);
            listModel.setWorkTypeText("");
            e.printStackTrace();
        }
    }

    private String getFormatDiscount(double value) {
        int discount = (int) Math.round(value);
        return discount == 0 ? "" : String.format(getActivity().getString(R.string.value_percent_off), String.valueOf(discount));
    }

    private String getFormatRouting(double distMiles, String neighborhood) {
        String distance;
        if (distMiles > 1)
            distance = new DecimalFormat("#.#").format(distMiles);
        else
            distance = new DecimalFormat("#.##").format(distMiles);
        return String.format(getActivity().getString(R.string.value_miles_to_neighborhood), distance, neighborhood);
    }
}
