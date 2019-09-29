package app.gateway.bars.locations.cache.clear;

import app.core.payment.regular.model.EmptyResponse;
import app.gateway.bars.list.cache.clear.ClearBarListInRealTimeGateway;
import app.gateway.bars.locations.cache.holder.LocationsListRealTimeHolder;
import rx.Observable;

public class ClearLocationListRealTimeGateway implements ClearBarListInRealTimeGateway {

    @Override public Observable<EmptyResponse> clear() {
       return Observable.create(subscriber -> {
            LocationsListRealTimeHolder.clear();
            subscriber.onNext(new EmptyResponse());
            subscriber.onCompleted();
        });
    }
}
