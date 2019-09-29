package app.delivering.mvp.payment.list.delete.binder;


import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;

import app.R;
import app.delivering.component.BaseActivity;
import rx.Observable;
import rx.Subscriber;

public class RxSnackBar {
    private BaseActivity activity;
    private Snackbar snackbar;

    public RxSnackBar(BaseActivity activity) {
        this.activity = activity;
    }

    public Observable<Boolean> show(View container, @StringRes int stringId) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override public void call(Subscriber<? super Boolean> subscriber) {
                String body = activity.getString(stringId);
                snackbar = Snackbar.make(container, body, Snackbar.LENGTH_LONG)
                        .setAction(activity.getString(R.string.undo), view -> {})
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            private boolean wasEvent;

                            @Override public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (this.wasEvent) return;
                                if (DISMISS_EVENT_ACTION == event)
                                    subscriber.onNext(true);
                                else
                                    subscriber.onNext(false);
                                this.wasEvent = true;
                                subscriber.onCompleted();
                            }

                            @Override public void onShown(Snackbar transientBottomBar) {

                            }
                        });

                snackbar.show();
            }
        });
    }
}
