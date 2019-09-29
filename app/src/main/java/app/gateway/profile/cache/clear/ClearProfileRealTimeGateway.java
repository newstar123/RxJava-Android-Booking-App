package app.gateway.profile.cache.clear;

import app.core.payment.regular.model.EmptyResponse;
import app.gateway.profile.cache.holder.ProfileRealTimeHolder;
import rx.Observable;

public class ClearProfileRealTimeGateway implements ClearProfileInRealTimeGateway {

    @Override public Observable<EmptyResponse> clear() {
       return Observable.create(subscriber -> {
            ProfileRealTimeHolder.clear();
            subscriber.onNext(new EmptyResponse());
            subscriber.onCompleted();
        });
    }
}
