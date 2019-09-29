package app.gateway.analytics.lift;

import android.content.Context;
import android.content.pm.PackageManager;

import com.google.firebase.analytics.FirebaseAnalytics;

import app.gateway.analytics.FirebaseLogGateway;
import rx.Observable;

public class CheckLiftFirebaseLogGateway implements FirebaseLogGateway {
    private static final String IS_LIFT_AVAILABLE = "is_lyft_app_available";
    private static final String LIFT_APP_NAME = "me.lyft.android";
    private Context context;

    public CheckLiftFirebaseLogGateway(Context context) {
        this.context = context;
    }

    @Override public Observable<Boolean> log() {
        return Observable.create(subscriber -> {
            PackageManager packageManager = context.getPackageManager();
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            boolean isLyftAvailable = false;
            try {
                packageManager.getPackageInfo(LIFT_APP_NAME, PackageManager.GET_ACTIVITIES);
                isLyftAvailable = true;
            } catch (PackageManager.NameNotFoundException e) {e.printStackTrace();}
            firebaseAnalytics.setUserProperty(IS_LIFT_AVAILABLE, String.valueOf(isLyftAvailable));
            subscriber.onNext(isLyftAvailable);
            subscriber.onCompleted();
        });
    }
}
