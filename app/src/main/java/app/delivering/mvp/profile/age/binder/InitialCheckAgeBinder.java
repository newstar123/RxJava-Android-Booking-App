package app.delivering.mvp.profile.age.binder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BaseBinder;
import app.delivering.mvp.dialog.RxDialogHandler;
import app.gateway.analytics.mixpanel.MixpanelSendGateway;
import app.gateway.analytics.mixpanel.events.MixpanelEvents;
import app.gateway.qorumcache.QorumSharedCache;
import app.gateway.qorumcache.basecache.BaseCacheType;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class InitialCheckAgeBinder extends BaseBinder {
    private static final String DATE_FORMAT_DAY = "dd";
    private static final String DATE_FORMAT_MONTH = "MM";
    private static final String DATE_FORMAT_YEAR = "yyyy";
    public static final int AGE_CHECKING = 58555;

    @BindView(R.id.editable_age_day) TextView day;
    @BindView(R.id.editable_age_month) TextView month;
    @BindView(R.id.editable_age_year) TextView year;

    private Date selectedDate;
    private Calendar initialCalendar;
    private Calendar selectCalendar;
    private RxDialogHandler rxDialogHandler;

    public InitialCheckAgeBinder(BaseActivity activity) {
        super(activity);
        rxDialogHandler = new RxDialogHandler(activity);
    }

    @Override
    public void afterViewsBounded() {
        initialCalendar = Calendar.getInstance();
        initialCalendar.add(Calendar.YEAR, -21);
    }

    @OnClick(R.id.editable_age_text)
    void onEditDateClick() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(), R.style.DatePickerDialogTheme, (datePicker, i, i1, i2) -> {
            selectCalendar = Calendar.getInstance();
            selectCalendar.set(i, i1, i2);
            selectedDate = selectCalendar.getTime();
            updateDate(selectCalendar);
        }, getActualCalendar().get(Calendar.YEAR), getActualCalendar().get(Calendar.MONTH), getActualCalendar().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private Calendar getActualCalendar() {
        if (selectCalendar == null)
            return initialCalendar;
        return selectCalendar;
    }

    private void updateDate(Calendar calendar) {
        String dayValue = (String) DateFormat.format(DATE_FORMAT_DAY, calendar.getTime());
        day.setText(dayValue);
        String monthNumber = (String) DateFormat.format(DATE_FORMAT_MONTH, calendar.getTime());
        month.setText(monthNumber);
        String yearValue = (String) DateFormat.format(DATE_FORMAT_YEAR, calendar.getTime());
        year.setText(yearValue);
    }

    @OnClick(R.id.initial_check_age_button)
    void onCheckAgeClick() {
        if (selectedDate == null) {
            rxDialogHandler
                    .showOneButtonWithoutTitle(R.string.please_check_birth_date, R.string.okay)
                    .subscribe(isOk -> { if (isOk) onEditDateClick(); }, e -> {}, () -> {});
        }
        else if (selectedDate.after(initialCalendar.getTime()))
            showErrorDialog();
        else {
            Observable.just(true)
                    .map(value -> QorumSharedCache.checkUserAge().save(BaseCacheType.BOOLEAN, value))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(isOk -> {
                        MixpanelSendGateway.sendWithSubscription(MixpanelEvents.getAgeVerificationPassedEvent());
                        finish();
                    }, e -> { }, () -> { });
        }
    }

    private void showErrorDialog() {
        rxDialogHandler
                .showOneButtonWithoutTitle(R.string.initial_check_age_error, R.string.okay)
                .subscribe(isOk -> { if (isOk) onEditDateClick(); }, e -> {}, () -> {});
    }

    private void finish() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
