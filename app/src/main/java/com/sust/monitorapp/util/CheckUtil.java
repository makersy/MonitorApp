package com.sust.monitorapp.util;

import com.sust.monitorapp.common.Constants;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yhl on 2020/3/5.
 * <p>
 * 格式检查工具类
 */
public class CheckUtil {

    /**
     * 检查用户名正确性
     * @param str 用户名
     * @return
     */
    public static boolean isUserName(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Constants.REGEX_USERNAME)) {
            return true;
        }
        return false;
    }

    /**
     * 检查密码正确性
     * @param str 密码
     * @return
     */
    public static boolean isPassword(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Constants.REGEX_PASSWORD)) {
            return true;
        }
        return false;
    }

    /**
     * 检查邮箱正确性
     * @param str 邮箱
     * @return
     */
    public static boolean isEmail(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Constants.REGEX_EMAIL)) {
            return true;
        }
        return false;
    }

    /**
     * 检查手机号正确性
     * @param str 手机号
     * @return
     */
    public static boolean isTel(String str) {
        if (StringUtils.isNoneBlank(str) && str.matches(Constants.REGEX_MOBILE_EXACT)) {
            return true;
        }
        return false;
    }

    /**
     * 检查绕组温度是否越界
     * @param raozuTem 绕组温度
     * @return
     */
    public static boolean raozuIsRight(float raozuTem) {

        return false;
    }

    /**
     * 检查油面温度是否越界
     * @param youmian 油面温度
     * @return
     */
    public static boolean youmianIsRight(float youmian) {

        return true;
    }
}
