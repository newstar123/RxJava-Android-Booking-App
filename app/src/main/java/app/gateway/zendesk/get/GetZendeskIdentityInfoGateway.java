package app.gateway.zendesk.get;

import android.content.Context;

import app.gateway.shared.base.BaseTextCacheInteger;
import app.gateway.shared.interfaces.GetIntegerValueGateway;
import app.gateway.zendesk.put.PutZendeskIdentityInfoGateway;
import rx.Observable;

public class GetZendeskIdentityInfoGateway implements GetIntegerValueGateway { // TODO: will be rewritten after checking zendesk implementation
    private final BaseTextCacheInteger cache;

    public GetZendeskIdentityInfoGateway(Context context) {
        cache = new BaseTextCacheInteger(context, PutZendeskIdentityInfoGateway.ZENDESK_USER_ID);
    }

    @Override
    public Observable<Integer> get() {
        return Observable.just(cache.get());
    }
}
