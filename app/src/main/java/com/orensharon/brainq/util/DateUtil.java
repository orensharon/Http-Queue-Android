package com.orensharon.brainq.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtil {

    private static SimpleDateFormat hourlyFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static SimpleDateFormat dailyFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static SimpleDateFormat weeklyFormat = new SimpleDateFormat("E", Locale.ENGLISH);

    public static SimpleDateFormat getHourlyDateInFormat() {
        return hourlyFormat;
    }

    public static SimpleDateFormat getDailyDateInFormat() {
        return dailyFormat;
    }

    public static SimpleDateFormat getWeeklyDateInFormat() {
        return weeklyFormat;
    }
}
