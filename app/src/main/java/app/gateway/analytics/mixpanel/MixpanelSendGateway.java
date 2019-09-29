package app.gateway.analytics.mixpanel;

import app.CustomApplication;
import rx.Observable;

public class MixpanelSendGateway {
    private static final String ANDROID_PREFIX = "[AND] ";

    public static Observable<Boolean> send(MixpanelLogModel model) {
        return Observable.create(subscriber -> {
            CustomApplication.get().getMixpanelInstance().track(ANDROID_PREFIX + model.getEventName(), model.getProperties());
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    public static void sendWithSubscription(MixpanelLogModel model) {
        send(model).subscribe(b->{}, r->{},()->{});
    }
}
