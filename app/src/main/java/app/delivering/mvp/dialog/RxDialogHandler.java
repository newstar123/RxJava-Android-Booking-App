package app.delivering.mvp.dialog;

import android.support.annotation.StringRes;
import android.view.View;

import app.delivering.component.BaseActivity;
import app.delivering.component.dialog.CustomDialog;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class RxDialogHandler {
    private CustomDialog customDialog;

    public RxDialogHandler(BaseActivity activity) {
        customDialog = new CustomDialog(activity);
    }

    public Observable<Boolean> showTwoButtonsWithoutTitle(@StringRes int message, @StringRes int negative, @StringRes int positive) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> customDialogSettings(subscriber, 0, message, negative, positive));
    }

    public Observable<Boolean> showTwoButtonsWithoutTitle(String message, @StringRes int negative, @StringRes int positive) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> customDialogSettings(subscriber, "", message, negative, positive));
    }

    public Observable<Boolean> showTwoButtonsWithTitle(@StringRes int title, @StringRes int message, @StringRes int negative, @StringRes int positive) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> customDialogSettings(subscriber, title, message, negative, positive));
    }

    public Observable<Boolean> showTwoButtonsWithTitle(String title, @StringRes int message, @StringRes int negative, @StringRes int positive) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> customDialogSettings(subscriber, title, message, negative, positive));
    }

    public Observable<Boolean> showTwoButtonsWithTitle(String title, String message, @StringRes int negative, @StringRes int positive) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> customDialogSettings(subscriber, title, message, negative, positive));
    }

    public Observable<Boolean> showOneButtonWithoutTitle(@StringRes int message, @StringRes int button) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> {
            customDialogSettings(subscriber, 0, message, 0, button);
        });
    }

    public Observable<Boolean> showOneButtonWithoutTitle(String message, @StringRes int button) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> { customDialogSettings(subscriber, "", message, 0, button); });
    }

    public Observable<Boolean> showOneButtonWithTitle(@StringRes int title, @StringRes int message, @StringRes int button) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> customDialogSettings(subscriber, title, message, 0, button));
    }

    public Observable<Boolean> showOneButtonWithTitle(String title, String message, @StringRes int button) {
        return Observable.create((Subscriber<? super Boolean> subscriber) -> customDialogSettings(subscriber, title, message, 0, button));
    }

    public void dismissDialog() {
        if (customDialog != null)
            customDialog.dismiss();
    }

    private void customDialogSettings(Subscriber<? super Boolean> subscriber, String title, @StringRes int message, int button1, int button2) {
        customDialog.show();
        customDialog.getBinder().setDialogStringTitle(title);
        customDialog.getBinder().setDialogMessage(message);
        setListeners(subscriber, button1, button2);
    }

    private void customDialogSettings(Subscriber<? super Boolean> subscriber, String title,
                                       String message, @StringRes int button1, @StringRes int button2) {
        customDialog.show();
        customDialog.getBinder().setDialogStringTitle(title);
        customDialog.getBinder().setDialogStringMessage(message);
        setListeners(subscriber, button1, button2);
    }

    private void customDialogSettings(Subscriber<? super Boolean> subscriber, @StringRes int title,
                                      @StringRes int message, @StringRes int button1, @StringRes int button2) {
        customDialog.show();
        customDialog.getBinder().setDialogTitle(title);
        customDialog.getBinder().setDialogMessage(message);
        setListeners(subscriber, button1, button2);
    }

    private void setListeners(Subscriber<? super Boolean> subscriber, @StringRes int button1, @StringRes int button2) {
        if (customDialog.getBinder().leftButton(button1).getVisibility() != View.GONE) {
            customDialog.getBinder().leftButton(button1).setOnClickListener(click -> {
                subscriber.onNext(false);
                subscriber.onCompleted();
            });
        }

        customDialog.getBinder().rightButton(button2).setOnClickListener(click -> {
            subscriber.onNext(true);
            subscriber.onCompleted();
        });

        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setOnCancelListener(listener -> customDialog.dismiss());
        subscriber.add(Subscriptions.create(customDialog::dismiss));
    }
}
