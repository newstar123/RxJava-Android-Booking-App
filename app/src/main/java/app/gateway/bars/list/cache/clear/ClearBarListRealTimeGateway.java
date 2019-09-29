package app.gateway.bars.list.cache.clear;

import app.core.payment.regular.model.EmptyResponse;
import app.gateway.bars.list.cache.holder.BarListRealTimeHolder;
import rx.Observable;

public class ClearBarListRealTimeGateway implements ClearBarListInRealTimeGateway {

    @Override public Observable<EmptyResponse> clear() {
       return Observable.create(subscriber -> {
            BarListRealTimeHolder.clear();
            subscriber.onNext(new EmptyResponse());
            subscriber.onCompleted();
        });
    }
}
