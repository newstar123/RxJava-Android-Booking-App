package app.delivering.mvp.bars.list.init.presenter;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.R;
import app.core.bars.list.get.entity.BarListModel;
import app.core.bars.list.get.entity.BarModel;
import app.core.bars.list.get.entity.CheckinedPersonModel;
import app.core.bars.list.get.interactor.GetBarListInteractor;
import app.core.checkin.update.CurrentCheckInInfo;
import app.core.checkin.update.UpdateCheckInfoInteractor;
import app.core.facebook.friends.entity.FacebookFriendModel;
import app.core.facebook.friends.interactor.GetFacebookFriendsInteractor;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.bars.list.init.enums.BarListFilterType;
import app.delivering.mvp.bars.list.init.model.SortedBarListModel;
import app.delivering.mvp.bars.work.BarWorkTypeParser;
import app.delivering.mvp.bars.work.model.BarWorkTypeModel;
import app.delivering.mvp.main.show.model.CitiesModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class GetBarListPresenter extends BasePresenter<CitiesModel, Observable<SortedBarListModel>> {
    private static final String NAME_ARTICLE = "The ";
    private static final String TRUE = "true";
    private static final String ON = "on";
    private static final String OK = "ok";
    private static final String NULL = "null";
    private GetBarListInteractor barListInteractor;
    private GetFacebookFriendsInteractor friendsInteractor;
    private UpdateCheckInfoInteractor updateCheckInfoInteractor;
    private BarListFilterType filterType;
    private boolean isOpenedTabExisting;

    public GetBarListPresenter(BaseActivity activity, BarListFilterType filterType) {
        super(activity);
        this.filterType = filterType;
        barListInteractor = new GetBarListInteractor(getActivity());
        friendsInteractor = new GetFacebookFriendsInteractor(activity);
        updateCheckInfoInteractor = new UpdateCheckInfoInteractor(getActivity());
    }

    @Override public Observable<SortedBarListModel> process(CitiesModel citiesModel) {
        return Observable.zip(barListInteractor.process(), friendsInteractor.process(),
                Observable.just((long) QorumSharedCache.checkFBUserId().get(BaseCacheType.LONG)), updateCheckInfoInteractor.process(),
                (barModels, friends, patronFacebookId, updateCheckIn) ->
                        TextUtils.isEmpty(citiesModel.getSelectCityName()) ? getFullList(barModels, friends, patronFacebookId, updateCheckIn)
                                : getListByCityName(barModels, friends, patronFacebookId, citiesModel.getSelectCityName(), updateCheckIn))
                .single()
                .concatMap(barList -> getSortedBarList(filterType, barList));
    }

    private ArrayList<BarListModel> getFullList(List<BarModel> barModels, List<FacebookFriendModel> friends,
                                                Long patronFacebookId, CurrentCheckInInfo currentCheckInInfo) {
        ArrayList<BarListModel> listModels = new ArrayList<>();
        for (BarModel barModel : barModels) {
            BarListModel listModel = createListModel(barModel, friends, patronFacebookId, currentCheckInInfo);
            listModels.add(listModel);
        }
        return listModels;
    }

    private ArrayList<BarListModel> getListByCityName(List<BarModel> barModels, List<FacebookFriendModel> friends,
                                                      Long patronFacebookId, String selectCityName, CurrentCheckInInfo currentCheckInInfo) {
        ArrayList<BarListModel> listModels = new ArrayList<>();
        for (BarModel barModel : barModels) {
            if (barModel.getLocation().getLabel().equals(selectCityName)) {
                BarListModel listModel = createListModel(barModel, friends, patronFacebookId, currentCheckInInfo);
                listModels.add(listModel);
            }
        }
        return listModels;
    }

    private BarListModel createListModel(BarModel barModel, List<FacebookFriendModel> friends,
                                         Long patronFacebookId, CurrentCheckInInfo currentCheckInInfo) {
        BarListModel listModel = new BarListModel();
        listModel.setId(barModel.getId());
        listModel.setName(barModel.getName());
        if (TextUtils.isEmpty(barModel.getBackgroundImageUrl()))
            listModel.setBackgroundUrl(barModel.getVideoThumbUrl());
        else
            listModel.setBackgroundUrl(barModel.getBackgroundImageUrl());
        listModel.setCity(barModel.getCity());
        listModel.setDistKm(barModel.getDistKm());
        listModel.setDistMiles(barModel.getDistMiles());
        listModel.setNeighborhood(barModel.getNeighborhood());
        listModel.setType(barModel.getType());
        listModel.setHours(barModel.getHours());
        listModel.setDiscount(barModel.getCurrentDiscount());
        listModel.setLatitude(barModel.getLatitude());
        listModel.setLongitude(barModel.getLongitude());
        listModel.setRouting(getFormatRouting(barModel.getDistMiles(), barModel.getNeighborhood()));
        listModel.setDiscountText(getFormatDiscount(barModel.getCurrentDiscount()));
        trySetWorkType(listModel, barModel);
        listModel.setSpecialNotice(barModel.getSpecialNotice());
        listModel.setSpecialNoticeStatus(barModel.getSpecialNoticeStatus());
        listModel.setCheckinedFriendsNumber(searchFriendsList(barModel, friends, patronFacebookId));

        if (listModel.getId() == currentCheckInInfo.getCheckInBarId() && currentCheckInInfo.getCheckInId() > 0) {
            listModel.setCheckInId(currentCheckInInfo.getCheckInBarId());
            QorumSharedCache.checkOpenedBarName().save(BaseCacheType.STRING, listModel.getName());
            isOpenedTabExisting = true;
        }
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

    private String getFormatRouting(double distMiles, String neighborhood) {
        String distance;
        if (distMiles > 1)
            distance = new DecimalFormat("#.#").format(distMiles);
        else
            distance = new DecimalFormat("#.##").format(distMiles);
        return String.format(getActivity().getString(R.string.value_miles_to_neighborhood), distance, neighborhood);
    }

    private String getFormatDiscount(double value) {
        int discount = (int) Math.round(value);
        return discount == 0 ? "" : String.format(getActivity().getString(R.string.value_percent_off), String.valueOf(discount));
    }

    private Observable<SortedBarListModel> getSortedBarList(BarListFilterType filterType, List<BarListModel> models) {
        return Observable.just((boolean)QorumSharedCache.checkArrowAnimation().get(BaseCacheType.BOOLEAN))
                .map(hasArrowsBeenShowed -> {
                    switch (filterType) {
                        case DISCOUNT:
                            Collections.sort(models, (model1, model2) -> Double.compare(model2.getDiscount(), model1.getDiscount()));
                            break;
                        case NAME:
                            Collections.sort(models, this::correctNameComparator);
                            break;
                        default:
                            Collections.sort(models, (model1, model2) -> Double.compare(model1.getDistMiles(), model2.getDistMiles()));
                    }
                    SortedBarListModel result = new SortedBarListModel(models, hasArrowsBeenShowed, isOpenedTabExisting);
                    isOpenedTabExisting = false;
                    return result;
                });
    }

    private int correctNameComparator(BarListModel model1, BarListModel model2) {
        String nameModel1 = model1.getName();
        if (nameModel1.startsWith(NAME_ARTICLE))
            nameModel1 = nameModel1.substring(4);
        String nameModel2 = model2.getName();
        if (nameModel2.startsWith(NAME_ARTICLE))
            nameModel2 = nameModel2.substring(4);
        return nameModel1.compareTo(nameModel2);
    }

    private int searchFriendsList(BarModel barModel, List<FacebookFriendModel> friends, Long patronFacebookId) {
        List<CheckinedPersonModel> checkedPeople = barModel.getCheckinedPeople();
        int friendsCount = 0;
        try {
            for (CheckinedPersonModel personModel : checkedPeople) {
                if (patronFacebookId != personModel.getFacebookId() && isFBActive(personModel.getIsFacebookVisible())) {
                    for (FacebookFriendModel friendModel : friends) {
                        if (personModel.getFacebookId() == friendModel.getId())
                            friendsCount++;
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return friendsCount;
    }

    private boolean isFBActive(String isFacebookVisible) {
        return isFacebookVisible.equalsIgnoreCase(TRUE)
                || isFacebookVisible.equalsIgnoreCase(ON)
                || isFacebookVisible.equalsIgnoreCase(OK)
                || isFacebookVisible.equalsIgnoreCase(NULL);

    }
}
