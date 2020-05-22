package sch.xmut.wu.apicourt.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemUtils {
    /**
     * 格式化时间 格式2020-05-04
     */
    public static String formatDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }
}
