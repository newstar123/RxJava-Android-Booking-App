package app.delivering.mvp.bars.list.item.branchclick.presenter;


import android.content.Intent;

import app.core.bars.detail.interactor.GetItemBarInteractor;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.mvp.BaseOutputPresenter;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class BarListItemClickBranchPresenter extends BaseOutputPresenter<Observable<Intent>> {
    private final GetItemBarInteractor getItemBarInteractor;

    public BarListItemClickBranchPresenter(BaseActivity activity) {
        super(activity);
        getItemBarInteractor = new GetItemBarInteractor(activity);
    }

    @Override
    public Observable<Intent> process() {
        return Observable.just((int) QorumSharedCache.checkBranch().get(BaseCacheType.INT))
                .filter(value -> value > 0)
                .concatMap(value -> getItemBarInteractor.process((long) value))
                .concatMap(this::markVenueDetailWasShownByCache)
                .single()
                .map(this::setUpIntent);
    }

    private Observable<BarModel> markVenueDetailWasShownByCache(BarModel barModel) {
        return Observable.just(QorumSharedCache.checkBranch().save(BaseCacheType.INT,-1))
                .map(emptyResponse -> barModel);
    }

    private Intent setUpIntent(BarModel barModel) {
        InitialVenueDetailModel initialModel = new InitialVenueDetailModel();
        initialModel.setBarId(barModel.getId());
        initialModel.setNameValue(barModel.getName());
        initialModel.setImage(barModel.getBackgroundImageUrl());
        initialModel.setDistance(String.valueOf(barModel.getDistMiles()));
        initialModel.setDistanceKm(barModel.getDistKm());
        initialModel.setDiscount(String.valueOf(barModel.getCurrentDiscount()));
        initialModel.setType(String.valueOf(barModel.getType()));

        return BarDetailActivity.getIntentWithExtras(getActivity(), initialModel);
    }
}
