package app.gateway.analytics.userid;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

import app.gateway.analytics.FirebaseLogGateway;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import rx.Observable;

public class UserIdFirebaseLogGateway implements FirebaseLogGateway {
    private static final String USER_ID = "LOCAL_USER_ID";
    private Context context;

    public UserIdFirebaseLogGateway(Context context) {
        this.context = context;
    }

    @Override public Observable<Boolean> log() {
        return Observable.just((long) QorumSharedCache.checkUserId().get(BaseCacheType.LONG))
                .concatMap(this::sendId);
    }

    private Observable<Boolean> sendId(Long id) {
        FirebaseAnalytics fireBase = FirebaseAnalytics.getInstance(context);
        fireBase.setUserProperty(USER_ID, String.valueOf(id));
        return Observable.just(true);
    }
}
