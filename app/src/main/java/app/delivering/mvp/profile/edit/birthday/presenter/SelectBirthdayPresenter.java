package app.delivering.mvp.profile.edit.birthday.presenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.profile.edit.birthday.exceptions.SelectBirthdayException;
import rx.Observable;
import rx.Subscriber;

public class SelectBirthdayPresenter extends BasePresenter<Date, Observable<String>> {

    public SelectBirthdayPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override public Observable<String> process(Date selectDate) {
        return Observable.create(subscriber -> {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.add(Calendar.YEAR, -21);
            Date minimalDate = startCalendar.getTime();
            if (minimalDate.before(selectDate))
                subscriber.onError(new SelectBirthdayException());
            else
                createStringDate(selectDate, subscriber);
        });
    }

    private void createStringDate(Date selectDate, Subscriber<? super String> subscriber) {
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String stringDate = format.format(selectDate);
        subscriber.onNext(stringDate);
        subscriber.onCompleted();
    }
}
