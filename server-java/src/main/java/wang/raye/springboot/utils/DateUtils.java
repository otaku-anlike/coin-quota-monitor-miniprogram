package wang.raye.springboot.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public final class DateUtils {
    private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";

    public DateUtils() {
    }

    public static String getDatePattern() {
        return defaultDatePattern;
    }

    public static String getToday() {
        Date today = new Date();
        return format(today);
    }

    public static String format(Date date) {
        return date == null ? " " : format(date, getDatePattern());
    }

    public static String format(Date date, String pattern) {
        return date == null ? " " : (new SimpleDateFormat(pattern)).format(date);
    }

    public static Date parse(String strDate) throws ParseException {
        return StringUtils.isEmpty(strDate) ? null : parse(strDate, getDatePattern());
    }

    public static Date parse(String strDate, String pattern) throws ParseException {
        return StringUtils.isEmpty(strDate) ? null : (new SimpleDateFormat(pattern)).parse(strDate);
    }

    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(2, n);
        return cal.getTime();
    }

    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, n);
        return cal.getTime();
    }

    public static Date addHour(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(11, n);
        return cal.getTime();
    }

    public static String getLastDayOfMonth(String year, String month) {
        Calendar cal = Calendar.getInstance();
        cal.set(1, Integer.parseInt(year));
        cal.set(5, 1);
        cal.add(2, 1);
        cal.add(5, -1);
        return String.valueOf(cal.get(5));
    }

    public static Date getDate(String year, String month, String day) throws ParseException {
        String result = year + "- " + (month.length() == 1 ? "0 " + month : month) + "- " + (day.length() == 1 ? "0 " + day : day);
        return parse(result);
    }

    public static String getMinutePoint(int limit, String pattern) {
        Calendar rightNow = Calendar.getInstance();
        int minute = rightNow.get(12);
        minute = Math.round((float)(minute / limit * limit));
        rightNow.set(12, minute);
        rightNow.set(13, 0);
        Date time = rightNow.getTime();
        return format(time, pattern);
    }

    public static Date getMinutePointDate(int limit) {
        Calendar rightNow = Calendar.getInstance();
        int minute = rightNow.get(12);
        minute = Math.round((float)(minute / limit * limit));
        rightNow.set(12, minute);
        rightNow.set(13, 0);
        Date time = rightNow.getTime();
        return time;
    }

    public static Date getPerMinutePointDate(int limit) {
        Calendar cal = Calendar.getInstance();
        cal.add(12, -limit);
        int minute = cal.get(12);
        minute = Math.round((float)(minute / limit * limit));
        cal.set(12, minute);
        cal.set(13, 0);
        Date time = cal.getTime();
        return time;
    }

    public static String getPerMinutePoint(int limit, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(12, -limit);
        int minute = cal.get(12);
        minute = Math.round((float)(minute / limit * limit));
        cal.set(12, minute);
        cal.set(13, 0);
        Date time = cal.getTime();
        return format(time, pattern);
    }

    public static String getBeforeDay(String pattern) {
        Calendar cl = Calendar.getInstance();
        int day = cl.get(5);
        cl.set(5, day - 1);
        Date time = cl.getTime();
        return format(time, pattern);
    }

    public static String getAfterDay(String pattern) {
        Calendar cl = Calendar.getInstance();
        int day = cl.get(5);
        cl.set(5, day + 1);
        Date time = cl.getTime();
        return format(time, pattern);
    }

    public static String getBeforeMonth(String pattern) {
        Calendar cl = Calendar.getInstance();
        int month = cl.get(2);
        cl.set(2, month - 1);
        Date time = cl.getTime();
        return format(time, pattern);
    }

    public static String getAfterMonth(String pattern) {
        Calendar cl = Calendar.getInstance();
        int month = cl.get(2);
        cl.set(2, month + 1);
        Date time = cl.getTime();
        return format(time, pattern);
    }

    public static void main(String[] args) {
        String adcode = "213100";
        if (adcode.endsWith("00")) {
            adcode = adcode.substring(0, 4) + "01";
        }

        System.out.println(adcode);
    }
}