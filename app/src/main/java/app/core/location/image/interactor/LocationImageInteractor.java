package app.core.location.image.interactor;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.core.BaseOutputInteractor;
import app.core.bars.locations.entity.LocationsModel;
import app.core.bars.locations.entity.event.LocationsModelEvent;
import app.core.bars.locations.entity.exceptions.TooFarLocationException;
import app.core.bars.locations.get.GetBarLocationListInteractor;
import app.core.bars.locations.near.GetNearBarLocationInteractor;
import app.delivering.component.BaseActivity;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class LocationImageInteractor implements BaseOutputInteractor<Observable<String>> {
    private final GetBarLocationListInteractor barLocationListInteractor;
    private final GetNearBarLocationInteractor nearBarLocationInteractor;

    public LocationImageInteractor(BaseActivity activity) {
        nearBarLocationInteractor = new GetNearBarLocationInteractor(activity);
        barLocationListInteractor = new GetBarLocationListInteractor(activity);
    }

    @Override
    public Observable<String> process() {
        return barLocationListInteractor.process()
                .doOnNext(this::postCities)
                .concatMap(this::findNearestLocationImageUrl);
    }

    private void postCities(List<LocationsModel> models) {
        EventBus.getDefault().postSticky(new LocationsModelEvent(models));
    }

    private Observable<String> findNearestLocationImageUrl(List<LocationsModel> locationsModels) {
        return nearBarLocationInteractor.process(locationsModels)
                .onErrorResumeNext(this::checkErrorType)
                .doOnNext(locationsModel -> QorumSharedCache.checkLocationImage().save(BaseCacheType.STRING, locationsModel.getImageUrl()))
                .map(LocationsModel::getImageUrl);
    }

    private Observable<? extends LocationsModel> checkErrorType(Throwable throwable) {
        if (throwable instanceof TooFarLocationException)
            return Observable.just(QorumSharedCache.checkLocationImage().save(BaseCacheType.STRING, throwable.getMessage()))
                    .doOnNext(s -> {
                        throw new TooFarLocationException(throwable.getMessage());
                    })
                    .map(s -> new LocationsModel());
        return Observable.error(throwable);
    }
}
