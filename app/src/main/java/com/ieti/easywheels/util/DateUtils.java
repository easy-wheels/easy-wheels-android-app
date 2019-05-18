package com.ieti.easywheels.util;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

    private static final Map<String, Integer> days;

    static {
        Map<String, Integer> aMap = new HashMap<>();
        aMap.put("Sunday", 0);
        aMap.put("Monday", 1);
        aMap.put("Tuesday", 2);
        aMap.put("Wednesday", 3);
        aMap.put("Thursday", 4);
        aMap.put("Friday", 5);
        aMap.put("Saturday", 6);
        days = Collections.unmodifiableMap(aMap);
    }

    public static Date getNextDateFromDayAndHour(String day, String hour) {
        GregorianCalendar nowDate = new GregorianCalendar();
        Integer dayOfWeek = days.get(day);
        int dayOfMonth = nowDate.get(Calendar.DAY_OF_MONTH) + (dayOfWeek + 7 - (nowDate.get(Calendar.DAY_OF_WEEK)-1)) % 7;
        String[] hourAndMinutes = hour.split(":");
        int hours = Integer.valueOf(hourAndMinutes[0]);
        int minutes = Integer.valueOf(hourAndMinutes[1]);
        GregorianCalendar editDate = new GregorianCalendar(nowDate.get(Calendar.YEAR), nowDate.get(Calendar.MONTH), dayOfMonth, hours, minutes, 0);
        if (editDate.getTime().getTime() < nowDate.getTime().getTime()) {
            editDate.add(Calendar.DAY_OF_MONTH, 7);
        }
        return editDate.getTime();
    }

    public static Date getDatePlusSeconds(Date date, int seconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

}
