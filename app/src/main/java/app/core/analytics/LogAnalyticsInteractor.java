package app.core.analytics;

import android.content.Context;

import app.core.BaseOutputInteractor;
import app.gateway.analytics.lift.CheckLiftFirebaseLogGateway;
import app.gateway.analytics.userid.UserIdFirebaseLogGateway;
import rx.Observable;

public class LogAnalyticsInteractor implements BaseOutputInteractor<Observable<Boolean>> {
    private final UserIdFirebaseLogGateway userIdFirebaseLogGateway;
    private final CheckLiftFirebaseLogGateway liftFirebaseLogGateway;


    public LogAnalyticsInteractor(Context context) {
        liftFirebaseLogGateway = new CheckLiftFirebaseLogGateway(context);
        userIdFirebaseLogGateway = new UserIdFirebaseLogGateway(context);
    }

    @Override public Observable<Boolean> process() {
        return userIdFirebaseLogGateway.log()
        .concatMap(isOk -> liftFirebaseLogGateway.log());
    }
}
