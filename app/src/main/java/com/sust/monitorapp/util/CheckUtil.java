package com.sust.monitorapp.util;

import com.sust.monitorapp.common.Const;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yhl on 2020/3/5.
 * <p>
 * 格式检查工具类
 */
public class CheckUtil {

    public static boolean isUserName(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Const.REGEX_USERNAME)) {
            return true;
        }
        return false;
    }

    public static boolean isPassword(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Const.REGEX_PASSWORD)) {
            return true;
        }
        return false;
    }

    public static boolean isEmail(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Const.REGEX_EMAIL)) {
            return true;
        }
        return false;
    }

    public static boolean isTel(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Const.REGEX_MOBILE_EXACT)) {
            return true;
        }
        return false;
    }
}
