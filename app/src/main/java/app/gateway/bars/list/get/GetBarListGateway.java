package app.gateway.bars.list.get;

import android.util.Log;

import java.util.List;

import app.core.bars.list.get.entity.BarModel;
import app.gateway.bars.list.cache.get.GetBarListRealTimeGateway;
import app.gateway.bars.list.cache.put.PutBarListInRealTimeGateway;
import app.gateway.bars.list.get.exceptions.EmptyRealTimeBarListException;
import app.gateway.bars.list.rest.GetBarListRestGateway;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;

public class GetBarListGateway implements GetBarsGateway {
    private GetBarListRealTimeGateway listRealTimeGateway;
    private GetBarListRestGateway listRestGateway;
    private PutBarListInRealTimeGateway putBarListInRealTimeGateway;

    public GetBarListGateway() {
        listRealTimeGateway = new GetBarListRealTimeGateway();
        listRestGateway = new GetBarListRestGateway();
        putBarListInRealTimeGateway = new PutBarListInRealTimeGateway();
    }

    @Override public Observable<List<BarModel>> get(double lat, double lng, int miles) {
        return listRealTimeGateway.get()
                .doOnNext(barModels -> {
                    if (barModels == null || barModels.isEmpty()) {
                        Log.d("GetBarListGateway", "listRealTimeGateway");
                        throw Exceptions.propagate(new EmptyRealTimeBarListException());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    if (throwable.getCause() instanceof EmptyRealTimeBarListException) {
                        Log.d("GetBarListGateway", "listRestGateway");
                        return getBarsFromRestAndSaveToRealtime(lat, lng, miles);
                    } else
                        return Observable.error(throwable.getCause());
                });
    }

    private Observable<List<BarModel>> getBarsFromRestAndSaveToRealtime(double lat, double lng, int miles) {
        return listRestGateway.get(lat, lng, miles)
                .concatMap(barModels -> putBarListInRealTimeGateway.put(barModels));
    }
}
