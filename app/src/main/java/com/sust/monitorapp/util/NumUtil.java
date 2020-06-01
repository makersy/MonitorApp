package com.sust.monitorapp.util;

/**
 * Created by yhl on 2020/5/20.
 */
public class NumUtil {

    /**
     * 16进制转10进制
     * @param hexString
     * @return
     */
    public static Integer hexValue(String hexString) {
//        return Integer.valueOf(hexString, 16);
        return Integer.valueOf(hexString);
    }
}
