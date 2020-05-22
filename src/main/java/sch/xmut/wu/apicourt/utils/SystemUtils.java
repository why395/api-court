package sch.xmut.wu.apicourt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemUtils {
    public static String formatDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String formatDate2(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
