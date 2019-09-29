package app.delivering.mvp.bars.detail.init.tablist.list.hours.presenter;

import android.text.TextUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.R;
import app.delivering.component.BaseActivity;
import app.delivering.mvp.BasePresenter;
import app.delivering.mvp.bars.detail.init.tablist.height.model.BarDetailHoursModel;
import app.delivering.mvp.bars.detail.init.tablist.list.hours.model.BarDetailWorkHoursModel;
import rx.Observable;

public class BarDetailHoursPresenter extends BasePresenter<BarDetailHoursModel, Observable<List<BarDetailWorkHoursModel>>> {
    private static final String SCHEDULE_SEPARATOR = "/";
    private final String[] week;

    public BarDetailHoursPresenter(BaseActivity activity) {
        super(activity);
        week = DateFormatSymbols.getInstance(Locale.US).getWeekdays();
    }

    @Override
    public Observable<List<BarDetailWorkHoursModel>> process(BarDetailHoursModel hoursModel) {
        return Observable.just(new ArrayList<BarDetailWorkHoursModel>())
                .map(modelList -> {
                    Calendar calendar = Calendar.getInstance();
                    String today = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
                    for (String dayOfWeek : week) {
                        if (!TextUtils.isEmpty(dayOfWeek))
                            modelList.add(parse(dayOfWeek, today, hoursModel));
                    }
                    return modelList;
                });
    }

    private BarDetailWorkHoursModel parse(String dayOfWeek, String today, BarDetailHoursModel hoursModel) {
        BarDetailWorkHoursModel model = new BarDetailWorkHoursModel();
        String startWorkTime = "";
        String endWorkTime = "";

        for (String scheduleDay : hoursModel.getList()) {
            String[] splits = scheduleDay.split(SCHEDULE_SEPARATOR);
            if (dayOfWeek.toLowerCase().contains(splits[0].toLowerCase())) {
                if (splits.length > 1)
                    startWorkTime = getFormattedTime(splits[1]);
                if (splits.length > 2)
                    endWorkTime = getFormattedTime(splits[2]);
            }
        }

        model.setDay(dayOfWeek);
        model.setTime((TextUtils.isEmpty(startWorkTime) && TextUtils.isEmpty(endWorkTime)) ? "" :
                        TextUtils.isEmpty(endWorkTime) ? String.format(getActivity().getString(R.string.value_dash_value), startWorkTime, "      ")
                                : String.format(getActivity().getString(R.string.value_dash_value), startWorkTime, endWorkTime));
        model.setToday(dayOfWeek.contains(today));
        model.setWorkType(hoursModel.getWorkTypeInformation());
        return model;
    }

    private String getFormattedTime(String time) {
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
