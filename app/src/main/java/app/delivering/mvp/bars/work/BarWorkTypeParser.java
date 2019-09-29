package app.delivering.mvp.bars.work;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.CustomApplication;
import app.R;
import app.core.bars.list.get.entity.BarModel;
import app.delivering.mvp.bars.list.init.enums.BarByWorkTime;
import app.delivering.mvp.bars.work.model.BarWorkTypeModel;

public class BarWorkTypeParser {
    private static final String SCHEDULE_SEPARATOR = "/";
    private static final String HOUR_MINUTE_SEPARATOR = ":";
    private static final String CLOSES_AT = "CLOSES AT %s";
    private static final String OPENS_AT = "OPENS AT %s";
    private static final String AM_PM_TIME_FORMAT = "KK:mm aa";
    private static final String AM_SUFFIX = "am";
    private static final String PM_SUFFIX = "pm";
    private static final String HOURS_COUNT_12 = "12";
    private static final String HOURS_COUNT_00 = "00";
    private static final int PM_HOURS_SPENT = 12;
    //test schedules
    private static final String[] T_SCH = {
           /* "rr/00:00/00:00",
            "rr/23:59/23:50",*/
            "rr/12:00 am/12:00 PM",
            "rr/12:00 am/12:00 am"/*,
            "rr/12:00 AM/01:00 pm",
            "rr/1:1 am/10:01am",
            "rr/1  :1 am/10:01am",
            "rr/23:10/10:00",
            "rr/00:00/01:00",
            "rr/17:00/19:00",
            "45/11:00/16:00",
            "rtyrtyr/18:10/02:00"*/
    };

    public static BarWorkTypeModel createWorkTypeInformation(BarModel barModel) {
        List<String> workHours = barModel.getHours();
        if (workHours != null && !workHours.isEmpty()) {
            String timezone = barModel.getTimezone();
            Calendar current = Calendar.getInstance(TimeZone.getTimeZone(timezone));
            String today = current.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
            List<String> hoursOfDays = barModel.getHours();
            for (String dayOfWeek : hoursOfDays) {
                if (dayOfWeek.contains(today))
                    return getBarWorkType(current, timezone, dayOfWeek);
            }
            return getClosedWorkTypeWithStartNextDay(current, hoursOfDays, timezone);
        } else
            return getClosedBarWorkType();
    }

    private static BarWorkTypeModel getBarWorkType(Calendar current, String timezone, String workTime) {
       // workTime = T_SCH[Math.abs(new Random().nextInt(T_SCH.length - 1))];
        String[] splits = workTime.split(SCHEDULE_SEPARATOR);
        if (isTimeFormatBroken(splits) || isVenueWorkingAllDay(splits))
            return openBarType();
        else if (isAmPmMarkPresent(splits[1]) || isAmPmMarkPresent(splits[2]))
            return workTypeSeeingAmPmState(current, timezone, splits);
        else
            return getWorkType(current, timezone, splits);
    }

    private static boolean isTimeFormatBroken(String[] splits) {
        return splits.length < 3; //Format - day/starTime/endTime
    }

    private static boolean isVenueWorkingAllDay(String[] splits) {
        return splits[1].equals(splits[2]); //Format - day/starTime/endTime
    }

    private static boolean isAmPmMarkPresent(String time) {
        return isValueContainsTarget(time, AM_SUFFIX) || isValueContainsTarget(time, PM_SUFFIX);
    }

    private static BarWorkTypeModel workTypeSeeingAmPmState(Calendar current, String timezone, String[] splits) {
        String startTimeIn24 = tryToConvertTo24Time(splits[1]);
        String endTimeIn24 = tryToConvertTo24Time(splits[2]);
        String[] convertedSchedule = {splits[0], startTimeIn24, endTimeIn24};
        return getWorkType(current, timezone, convertedSchedule);
    }

    private static String tryToConvertTo24Time(String timeString) {
        String timeIn24String = timeString;
        if (isValueContainsTarget(timeString, AM_SUFFIX)) {
            if (timeString.startsWith(HOURS_COUNT_12)){
                timeString = timeString.replaceAll(HOURS_COUNT_12, HOURS_COUNT_00);
            }
            String[] startTimeParts = timeString.split(HOUR_MINUTE_SEPARATOR);
            timeIn24String = startTimeParts[0].trim() + HOUR_MINUTE_SEPARATOR + startTimeParts[1].substring(0, 2).trim();
        } else if (isValueContainsTarget(timeString, PM_SUFFIX)){
            String[] startTimeParts = timeString.split(HOUR_MINUTE_SEPARATOR);
            if (timeString.startsWith(HOURS_COUNT_12)){
                timeIn24String = startTimeParts[0].trim() + HOUR_MINUTE_SEPARATOR + startTimeParts[1].substring(0, 2).trim();
            } else {
                int timeIn24 = Integer.parseInt(startTimeParts[0].trim()) + PM_HOURS_SPENT;
                timeIn24String = String.valueOf(timeIn24) + HOUR_MINUTE_SEPARATOR + startTimeParts[1].substring(0, 2).trim();
            }
        }
        return timeIn24String;
    }

    private static boolean isValueContainsTarget(String value, String target){
        return value.toLowerCase().contains(target);
    }

    private static BarWorkTypeModel getWorkType(Calendar current, String timezone, String[] splits) {
        Calendar startWorkCalendar = getCalendarWith(timezone, splits[1].split(HOUR_MINUTE_SEPARATOR));
        Calendar endWorkCalendar = getCalendarWith(timezone, splits[2].split(HOUR_MINUTE_SEPARATOR));

        if (startWorkCalendar.getTime().after(endWorkCalendar.getTime()))
            endWorkCalendar.add(Calendar.HOUR, 24);

        if (current.getTime().after(startWorkCalendar.getTime()) && current.getTime().before(endWorkCalendar.getTime()))
            return checkOpenBarWorkType(current, endWorkCalendar);
        else
            return getClosedBarWorkType(startWorkCalendar);
    }

    private static Calendar getCalendarWith(String timezone, String[] values) {
        int hours = Integer.parseInt(values[0]);
        int minutes = Integer.parseInt(values[1]);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private static BarWorkTypeModel checkOpenBarWorkType(Calendar current, Calendar endWorkCalendar) {
        long timeDelta = endWorkCalendar.getTimeInMillis() - current.getTimeInMillis();
        if (Math.abs(timeDelta) <= 30 * 60 * 1000){
            BarWorkTypeModel workTypeModel = new BarWorkTypeModel();
            workTypeModel.setBarWorkTimeType(BarByWorkTime.CLOSES_SOON);
            workTypeModel.setWorkTypeText(getAmPmTime(CLOSES_AT, endWorkCalendar));
            return workTypeModel;
        } else
            return openBarType();
    }

    private static BarWorkTypeModel openBarType() {
        BarWorkTypeModel workTypeModel = new BarWorkTypeModel();
        workTypeModel.setBarWorkTimeType(BarByWorkTime.OPEN);
        return workTypeModel;
    }

    private static BarWorkTypeModel getClosedBarWorkType(Calendar startWork) {
        BarWorkTypeModel workTypeModel = new BarWorkTypeModel();
        workTypeModel.setBarWorkTimeType(BarByWorkTime.CLOSED);
        workTypeModel.setWorkTypeText(getAmPmTime(OPENS_AT, startWork));
        return workTypeModel;
    }

    private static BarWorkTypeModel getClosedWorkTypeWithStartNextDay(Calendar current, List<String> hoursOfDays, String timezone) {
        Calendar nextWorkDay = getNextWorkDayStart(current, hoursOfDays, timezone);
        if (nextWorkDay.getTimeInMillis() == current.getTimeInMillis()){
            return getClosedBarWorkType();
        } else {
            BarWorkTypeModel workTypeModel = new BarWorkTypeModel();
            workTypeModel.setBarWorkTimeType(BarByWorkTime.CLOSED);
            workTypeModel.setWorkTypeText(getAmPmTime(OPENS_AT, nextWorkDay));
            return workTypeModel;
        }
    }

    private static Calendar getNextWorkDayStart(Calendar current, List<String> hoursOfDays, String timezone) {
        for (int i = 0; i < 7; i++){
            current.add(Calendar.HOUR, 24);
            String nextDay = current.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
            for (String dayOfWeek : hoursOfDays) {
                String[] splits = dayOfWeek.split(SCHEDULE_SEPARATOR);
                if (dayOfWeek.contains(nextDay) && splits.length > 1)
                    return getCalendarWith(timezone, tryToConvertTo24Time(splits[1]).split(HOUR_MINUTE_SEPARATOR));
            }
        }
        return current;
    }

    private static BarWorkTypeModel getClosedBarWorkType() {
        BarWorkTypeModel workTypeModel = new BarWorkTypeModel();
        workTypeModel.setBarWorkTimeType(BarByWorkTime.CLOSED);
        workTypeModel.setWorkTypeText(CustomApplication.get().getString(R.string.word_closed));
        return workTypeModel;
    }

    private static String getAmPmTime(String format, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AM_PM_TIME_FORMAT, Locale.US);
        TimeZone tz = calendar.getTimeZone();
        dateFormat.setTimeZone(tz);
        String time = dateFormat.format(calendar.getTime());
        return String.format(format, time);
    }
}