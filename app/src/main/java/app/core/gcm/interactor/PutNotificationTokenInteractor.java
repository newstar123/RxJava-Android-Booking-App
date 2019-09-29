package app.core.gcm.interactor;

import com.google.firebase.iid.FirebaseInstanceId;

import app.core.BaseOutputInteractor;
import app.core.payment.regular.model.EmptyResponse;
import app.delivering.component.BaseActivity;
import app.gateway.gcm.PutGcmTokenRestGateway;
import rx.Observable;

public class PutNotificationTokenInteractor implements BaseOutputInteractor<Observable<EmptyResponse>> {
    private PutGcmTokenRestGateway putGcmTokenRestGateway;
    private BaseActivity activity;

    public PutNotificationTokenInteractor(BaseActivity activity) {
        putGcmTokenRestGateway = new PutGcmTokenRestGateway(activity);
        this.activity = activity;
    }

    @Override
    public Observable<EmptyResponse> process() {
        return getToken()
        .concatMap(s -> putGcmTokenRestGateway.put(s));
    }

    private Observable<String> getToken() {
        return Observable.create(subscriber ->
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, instanceIdResult -> {
                    subscriber.onNext(instanceIdResult.getToken());
                    subscriber.onCompleted();
                }));
    }
}
