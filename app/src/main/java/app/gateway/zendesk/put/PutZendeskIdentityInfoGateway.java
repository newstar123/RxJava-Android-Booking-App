package app.gateway.zendesk.put;

import android.content.Context;

import app.gateway.shared.base.BaseTextCacheInteger;
import app.gateway.shared.interfaces.PutIntegerValueGateway;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PutZendeskIdentityInfoGateway implements PutIntegerValueGateway { // TODO: will be rewritten after checking zendesk implementation
    private final BaseTextCacheInteger cache;
    public static final String ZENDESK_USER_ID = "ZENDESK_USER_ID";


    public PutZendeskIdentityInfoGateway(Context context) {
        cache = new BaseTextCacheInteger(context, ZENDESK_USER_ID);
    }

    @Override public Observable<Integer> put(int value) {
        return Observable.just(value)
                .doOnNext(v -> cache.save(value))
                .subscribeOn(Schedulers.io());
    }
}