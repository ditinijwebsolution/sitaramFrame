package com.om.sitaramfrem.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {
    public static final String FORMAT_TODAY = "Today";
    public static final String FORMAT_TOMORROW = "Tomorrow";
    public static final String FORMAT_YESTERDAY = "Yesterday";

    public static final String FORMAT_DAY = "dd";
    public static final String FORMAT_DAY_SHORT_NAME = "EEE";
    public static final String FORMAT_DAY_FULL_NAME = "EEEE";
    public static final String FORMAT_MONTH = "MM";
    public static final String FORMAT_MONTH_SHORT_NAME = "MMM";
    public static final String FORMAT_MONTH_FULL_NAME = "MMMM";
    public static final String FORMAT_YEAR = "yyyy";

    public static final String DISPLAY_DATE_FORMAT = "dd-MM-yyyy";
    public static final String DISPLAY_TIME_FORMAT = "hh:mm a";
    public static final String AUDIO_DATE_TIME_FORMAT = "dd-MM-yyyy hh:mma";

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SERVER_TIME_FORMAT = "HH:mm";
    public static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SERVER_TIME_SECOND_FORMAT = "HH:mm:ss";

    public static final String TIME_ZONE_UTC = "UTC";
    public static final String TIME_ZONE_ASIA_KOLKATA = "Asia/Kolkata";

    public static final String DATE_FORMATE_CUSTOM_CALENDAR_TITLE = "MMMM yyyy";

    public static Calendar getCalendarWithFullTime() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_ASIA_KOLKATA));
        return mCalendar;
    }

    public static Calendar getCalendarInUtcTimeZone() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));

        return mCalendar;
    }

    public static Calendar getCalendarFromString(String logDate, String format) {
        Calendar mCalendar = Calendar.getInstance();

        try {
            SimpleDateFormat smDateFormat = new SimpleDateFormat(format);
            mCalendar.setTime(smDateFormat.parse(logDate));
            //mCalendar.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_ASIA_KOLKATA));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mCalendar;
    }

    public static Calendar getCalendarFromUtcString(String logDate, String format) {
        Calendar mCalendar = Calendar.getInstance();

        try {
            SimpleDateFormat smDateFormat = new SimpleDateFormat(format);
            smDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));

            mCalendar.setTime(smDateFormat.parse(logDate));
            mCalendar.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_ASIA_KOLKATA));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mCalendar;
    }

    public static Calendar getCalendarWithUtcTimeZone(String logDate, String format) {
        Calendar mCalendar = Calendar.getInstance();

        try {
            SimpleDateFormat smDateFormat = new SimpleDateFormat(format);
            mCalendar.setTime(smDateFormat.parse(logDate));
            mCalendar.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_UTC));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mCalendar;
    }

    public static String getStringFromCalendar(Calendar mCalendar, String dateFormat) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat(dateFormat);
        // simpleDateformat.setTimeZone(mCalendar.getTimeZone());

        return simpleDateformat.format(mCalendar.getTime());
    }

    public static Calendar getTodayCalendarObject() {
        Calendar mCalendar = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        return mCalendar;
    }

    public static Calendar getTodayCalendarWithTime() {
        Calendar mCalendar = Calendar.getInstance();

       /* mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);*/

        return mCalendar;
    }

    public static Calendar getSpecificDayWithOffset(Calendar mSelectedDate, int offset) {
        Calendar mCalendar = (Calendar) mSelectedDate.clone();

        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);

        mCalendar.add(Calendar.DAY_OF_MONTH, offset);

        return mCalendar;
    }

    public static Calendar getFirstDayOfWeek(Calendar mSelectedDate) {
        Calendar mCalendar = (Calendar) mSelectedDate.clone();

        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);
        mCalendar.set(Calendar.DAY_OF_WEEK, mCalendar.getFirstDayOfWeek());

        return mCalendar;
    }

    public static Calendar getLastDayOfWeek(Calendar mSelectedDate) {
        Calendar mCalendar = (Calendar) mSelectedDate.clone();

        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);
        mCalendar.set(Calendar.DAY_OF_WEEK, mCalendar.getFirstDayOfWeek());
        mCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        mCalendar.add(Calendar.MILLISECOND, -1);

        return mCalendar;
    }

    public static Calendar getFirstDayOfMonth(Calendar mSelectedDate) {
        Calendar mCalendar = (Calendar) mSelectedDate.clone();

        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);

        return mCalendar;
    }

    public static Calendar getLastDayOfMonth(Calendar mSelectedDate) {
        Calendar mCalendar = (Calendar) mSelectedDate.clone();

        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);
        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return mCalendar;
    }

    public static Calendar getNextPreviousMonth(Calendar mSelectedDate, int offset) {
        Calendar mCalendar = (Calendar) mSelectedDate.clone();

        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
        mCalendar.clear(Calendar.MINUTE);
        mCalendar.clear(Calendar.SECOND);
        mCalendar.clear(Calendar.MILLISECOND);

        mCalendar.add(Calendar.MONTH, offset);

        return mCalendar;
    }

    public static long getDaysBetween(Calendar startDate, Calendar endDate) {
        Calendar newStart = Calendar.getInstance();
        newStart.setTimeInMillis(startDate.getTimeInMillis());
        newStart.set(Calendar.HOUR_OF_DAY, 0);
        newStart.set(Calendar.MINUTE, 0);
        newStart.set(Calendar.SECOND, 0);
        newStart.set(Calendar.MILLISECOND, 0);

        Calendar newEnd = Calendar.getInstance();
        newEnd.setTimeInMillis(endDate.getTimeInMillis());
        newEnd.set(Calendar.HOUR_OF_DAY, 0);
        newEnd.set(Calendar.MINUTE, 0);
        newEnd.set(Calendar.SECOND, 0);
        newEnd.set(Calendar.MILLISECOND, 0);

        return TimeUnit.MILLISECONDS.toDays(Math.abs(newEnd.getTimeInMillis() - newStart.getTimeInMillis()));
    }

    public static long getDaysBetweenWithoutAbs(Calendar startDate, Calendar endDate) {
        Calendar newStart = Calendar.getInstance();
        newStart.setTimeInMillis(startDate.getTimeInMillis());
        newStart.set(Calendar.HOUR_OF_DAY, 0);
        newStart.set(Calendar.MINUTE, 0);
        newStart.set(Calendar.SECOND, 0);
        newStart.set(Calendar.MILLISECOND, 0);

        Calendar newEnd = Calendar.getInstance();
        newEnd.setTimeInMillis(endDate.getTimeInMillis());
        newEnd.set(Calendar.HOUR_OF_DAY, 0);
        newEnd.set(Calendar.MINUTE, 0);
        newEnd.set(Calendar.SECOND, 0);
        newEnd.set(Calendar.MILLISECOND, 0);

        return TimeUnit.MILLISECONDS.toDays(newEnd.getTimeInMillis() - newStart.getTimeInMillis());
    }

    public static String getPillSelectedDateFormat() {
        return FORMAT_MONTH_SHORT_NAME + " " + FORMAT_DAY;
    }

    public static boolean isTodayDate(Calendar selectedCalendar) {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DATE) == selectedCalendar.get(Calendar.DATE)) {
            return true;
        }
        return false;
    }

    public static boolean isYesterdayDate(Calendar selectedCalendar) {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DATE) - selectedCalendar.get(Calendar.DATE) == 1) {
            return true;
        }
        return false;
    }
}
