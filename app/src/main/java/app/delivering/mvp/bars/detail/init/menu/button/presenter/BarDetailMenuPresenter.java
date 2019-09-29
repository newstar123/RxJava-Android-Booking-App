package app.delivering.mvp.bars.detail.init.menu.button.presenter;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.R;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.menu.button.model.MenuKitchenWorkTimeModel;
import app.delivering.mvp.bars.detail.init.menu.button.presenter.exceptions.KitchenNotWorkTodayException;
import app.delivering.mvp.bars.detail.init.menu.button.presenter.exceptions.KitchenWorkTimeEmptyException;
import rx.Observable;
import rx.Subscriber;

public class BarDetailMenuPresenter extends BasePresenter<BarModel, Observable<MenuKitchenWorkTimeModel>> {
    private static final String AM_SUFFIX = "am";
    private static final String PM_SUFFIX = "pm";

    public BarDetailMenuPresenter(BaseActivity activity) {
        super(activity);
    }

    @Override public Observable<MenuKitchenWorkTimeModel> process(BarModel barModel) {
        return Observable.create(subscriber -> {
            List<String> timesList = barModel.getKitchenHours();
            if (timesList == null || timesList.isEmpty())
                subscriber.onError(new KitchenWorkTimeEmptyException());
            MenuKitchenWorkTimeModel model = new MenuKitchenWorkTimeModel();
            model.setKitchenWorkTime(getTodayWorkInterval(subscriber, timesList));
            subscriber.onNext(model);
            subscriber.onCompleted();
        });
    }

    private String getTodayWorkInterval(Subscriber<? super MenuKitchenWorkTimeModel> subscriber, List<String> timesList) {
        String todayWorkInterval = "";
        Calendar calendar = Calendar.getInstance();
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
        for (String serverFormatTime : timesList) {
            String[] splits = serverFormatTime.split("/");
            if (splits.length > 2 && splits[0].equals(day)){
                todayWorkInterval = getWorkInterval(splits[1], splits[2]);
            }
        }
        if (TextUtils.isEmpty(todayWorkInterval))
            subscriber.onError(new KitchenNotWorkTodayException());
        return todayWorkInterval;
    }

    private String getWorkInterval(String start, String end) {
        String formatStartTime = getFormattedTime(start);
        String formatEndTime = getFormattedTime(end);
        return String.format(getActivity().getString(R.string.value_dash_value), formatStartTime, formatEndTime);
    }

    private String getFormattedTime(String time) {
        if (time.contains(AM_SUFFIX) || time.contains(PM_SUFFIX))
            return time;
        SimpleDateFormat serverFormat = new SimpleDateFormat("HH:mm", Locale.US);
        SimpleDateFormat clientFormat = new SimpleDateFormat("h:mm a", Locale.US);
        try {
            Date date = serverFormat.parse(time);
            return clientFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
