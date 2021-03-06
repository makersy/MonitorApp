package com.sust.monitorapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by yhl on 2020/3/12.
 */
public class DateUtil {
    public static String formatDate(String str) {
        //自定义的时间格式化
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sf2 = new SimpleDateFormat("MM/dd HH:mm");
        String formatStr = "";
        try {
            formatStr = sf1.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }
}
