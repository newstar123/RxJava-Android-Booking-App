package app.gateway.bars.locations.get;

import android.util.Log;

import java.util.List;

import app.core.bars.locations.entity.LocationsModel;
import app.gateway.bars.locations.cache.get.GetLocationListRealTimeGateway;
import app.gateway.bars.locations.cache.put.PutLocationsListInRealTimeGateway;
import app.gateway.bars.locations.get.exception.EmptyRealTimeLocationsListException;
import app.gateway.bars.locations.rest.GetBarLocationListGateway;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;

public class GetCommonLocationListGateway implements GetCommonLocationsGateway {
    private GetLocationListRealTimeGateway listRealTimeGateway;
    private GetBarLocationListGateway listRestGateway;
    private PutLocationsListInRealTimeGateway putInRealTimeGateway;

    public GetCommonLocationListGateway() {
        listRealTimeGateway = new GetLocationListRealTimeGateway();
        listRestGateway = new GetBarLocationListGateway();
        putInRealTimeGateway = new PutLocationsListInRealTimeGateway();
    }

    @Override public Observable<List<LocationsModel>> get() {
        return listRealTimeGateway.get()
                .doOnNext(models -> {
                    if (models == null || models.isEmpty()) {
                        Log.d("Gateway", "GetLocationListRealTimeGateway");
                        throw Exceptions.propagate(new EmptyRealTimeLocationsListException());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext(throwable -> {
                    if (throwable instanceof EmptyRealTimeLocationsListException) {
                        Log.d("Gateway", "GetBarLocationListGateway");
                        return getFromRestAndSaveToRealtime();
                    } else
                        return Observable.error(throwable.getCause());
                });
    }

    private Observable<List<LocationsModel>> getFromRestAndSaveToRealtime() {
        return listRestGateway.get()
                .flatMap(Observable::from)
                .filter(LocationsModel::isActive)
                .toList()
                .concatMap(models -> putInRealTimeGateway.put(models));
    }

}
