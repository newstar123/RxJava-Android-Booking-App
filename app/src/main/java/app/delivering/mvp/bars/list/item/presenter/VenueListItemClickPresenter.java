package app.delivering.mvp.bars.list.item.presenter;

import android.content.Intent;

import app.delivering.component.BaseActivity;
import app.delivering.component.bar.detail.BarDetailActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.model.InitialVenueDetailModel;
import app.delivering.mvp.bars.list.item.click.events.OnBarItemClickEvent;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class VenueListItemClickPresenter extends BasePresenter<OnBarItemClickEvent, Observable<Intent>> {

    public VenueListItemClickPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override
    public Observable<Intent> process(OnBarItemClickEvent onBarItemClickEvent) {
        if (onBarItemClickEvent.getShouldOpenBySwiping())
            return Observable.just(QorumSharedCache.checkArrowAnimation().save(BaseCacheType.BOOLEAN, true))
                    .map(isAlreadyShown -> prepareIntent(onBarItemClickEvent));
        else
            return Observable.just(onBarItemClickEvent)
                .map(this::prepareIntent);
    }

    private Intent prepareIntent(OnBarItemClickEvent event) {
        InitialVenueDetailModel initialModel = new InitialVenueDetailModel();
        initialModel.setBarId(event.getBarId());
        initialModel.setNameValue(event.getName() == null ? event.getNameValue() : event.getName().getText().toString());
        initialModel.setImage(event.getImage() == null ? "" : event.getImage());
        initialModel.setDistance(event.getDistance());
        initialModel.setDistanceKm(event.getDistanceKm());
        initialModel.setDiscount(event.getDiscount() == null ? "" : event.getDiscount().getText().toString());
        initialModel.setBarWorkType(event.getBarWorkType() == null ? "" : event.getBarWorkType().name());
        initialModel.setType(event.getType() == null ? "" : event.getType().getText().toString());
        initialModel.setSwipingText(event.getSwipingText());
        initialModel.setShouldOpenBySwiping(event.getShouldOpenBySwiping());
        return BarDetailActivity.getIntentWithExtras(getActivity(), initialModel);
    }
}
