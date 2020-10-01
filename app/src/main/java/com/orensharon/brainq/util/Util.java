package com.orensharon.brainq.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Util {

    private static SimpleDateFormat format;

    public static SimpleDateFormat getSimpleDateFormat() {
        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        }
        return format;
    }

}
