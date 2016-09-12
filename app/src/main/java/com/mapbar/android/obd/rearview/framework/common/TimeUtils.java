package com.mapbar.android.obd.rearview.framework.common;

import android.content.res.Resources;
import android.text.TextUtils;

import com.mapbar.android.obd.rearview.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yun on 16/1/7.
 */
public class TimeUtils {

    public static final int MILLIS_OF_DAY = 24 * 60 * 60 * 1000;
    public static final int DAYS_OF_WEEK = 7;


    public static long getCurrentTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    public static long getTodayTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    public static int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return isLeapYear(year) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    public static boolean isLeapYear(int year) {
        boolean isLeapYear = false;
        if (year % 400 == 0) {
            isLeapYear = true;
        } else if (year % 4 == 0 && year % 100 != 0) {
            isLeapYear = true;
        }
        return isLeapYear;
    }

    public static int getYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH);
    }

    public static boolean isSameDay(Calendar day1, Calendar day2) {
        if (day1 == null || day2 == null) {
            return false;
        }
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR)
                && day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
                && day1.get(Calendar.DAY_OF_MONTH) == day2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameDay(Calendar day1, int year, int monthOfYear, int dayOfMonth) {
        if (day1 == null) {
            return false;
        }
        return day1.get(Calendar.YEAR) == year
                && day1.get(Calendar.MONTH) == monthOfYear
                && day1.get(Calendar.DAY_OF_MONTH) == dayOfMonth;
    }

    public static String getDayOfWeek(int dayOfWeek) {
        return getResources().getStringArray(R.array.day_of_week1)[dayOfWeek - 1];
    }


    public static int getDayOfWeekInt(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getDayOfWeekText(long time) {
        return getDayOfWeek(getDayOfWeekInt(time));
    }

    private static int getDayOfMonthInt(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String getDateHHMM(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_hhmm), Locale.CHINA)
                .format(time);
    }

    public static String getDateHHMMssSSS(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_MMddHHmmssSSS), Locale.CHINA)
                .format(time);
    }

    public static String getDateHHMMss(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.data_format_HHmmss), Locale.CHINA)
                .format(time);
    }

    public static String getDateMMDD(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_mmdd), Locale.CHINA)
                .format(time);
    }

    public static String getDataMMDDHHMM(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_mmddhhmm),
                Locale.CHINA).format(time);
    }

    public static String getDateAll(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmdd),
                Locale.CHINA).format(time);
    }

    public static String getDateYYMMDDHHMM(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmddhhmm),
                Locale.CHINA).format(time);
    }

    public static String getDateYYMMDDHHMM2(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmddhhmm2),
                Locale.CHINA).format(time);
    }

    public static String getmDateYYYYMMDD2(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmdd2),
                Locale.CHINA).format(time);
    }

    public static String getDateYYYYMMDD5(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmdd5),
                Locale.CHINA).format(time);
    }

    public static String getDataYYYYMMDDSS(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmddhhmmss),
                Locale.CHINA).format(time);
    }

    public static String getDataYYYYMMDDSS2(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmddhhmmss2),
                Locale.CHINA).format(time);
    }

    public static String getDataYYYYMMDDSS3(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmddhhmmss3),
                Locale.CHINA).format(time);
    }

    public static String getmDateYYYYMMDD3(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmdd3),
                Locale.CHINA).format(time);
    }

    public static String getmDateYYYYMMDD4(long time) {
        return new SimpleDateFormat(getResources().getString(R.string.date_format_yyyymmdd4),
                Locale.CHINA).format(time);
    }

    public static Calendar birthdayToCalendar(String birthday) {
        final Calendar c = Calendar.getInstance();
        if (!TextUtils.isEmpty(birthday)) {
            final String[] birthArray = birthday.split("-");
            if (birthArray.length == 3) {
                c.set(
                        Integer.valueOf(birthArray[0]),
                        Integer.valueOf(birthArray[1]) - 1,
                        Integer.valueOf(birthArray[2]));
                return c;
            }
        }
        return null;
    }

    private static Resources getResources() {
        return Global.getAppContext().getResources();
    }

    public static long parseStringToLong(String dateStr, String pattern) {
        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(pattern)) {
            return 0;
        }
        SimpleDateFormat formater = new SimpleDateFormat(pattern);
        try {
            Date date = formater.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String parseTime(long time) {
        long temphour = time / (60 * 60 * 1000);

        String hour;
        if (temphour < 10) {
            hour = "0" + String.valueOf(temphour);
        } else {
            hour = String.valueOf(temphour);
        }
        long tempminute = (time - (temphour * 60 * 60 * 1000)) / (60 * 1000);
        String minute;
        if (tempminute < 10) {
            minute = "0" + String.valueOf(tempminute);
        } else {
            minute = String.valueOf(tempminute);
        }

        long tempsecond = (time - (temphour * 60 * 60 * 1000) - (tempminute * 60 * 1000)) / 1000;
        String second;
        if (tempsecond < 10) {
            second = "0" + String.valueOf(tempsecond);
        } else {
            second = String.valueOf(tempsecond);
        }
        //TimeFormatUtil.formatHHmmss(time)
        return hour + ":" + minute;
    }
}
