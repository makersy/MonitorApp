package com.sust.monitorapp.util;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.sust.monitorapp.common.MyApplication;



/**
 * Created by yhl on 2020/2/25.
 *
 * 提供处理一些 UI相关问题而创建的工具类，提供资源获取的通用方法，避免每次都写重复的代码获取结果
 */

public class UIUtils {

    public static Context getContext() {
        return MyApplication.context;
    }

    public static Handler getHandler() {
        return MyApplication.handler;
    }

    /**
     * 返回指定 colorId的颜色值
     * @param colorId color文件中某个 color的 id
     * @return 该 color的色值
     */
    public static int getColor(int colorId) {
        return getContext().getResources().getColor(colorId);
    }

    /**
     * 加载指定 viewId 的视图对象，并返回
     * @param viewId
     * @return
     */
    public static View getView(int viewId) {
        View view = View.inflate(getContext(), viewId, null);
        return view;
    }

    public static String[] getStringArr(int strArrId) {
        String[] stringArray = getContext().getResources().getStringArray(strArrId);
        return stringArray;
    }

    /**
     * 将 dp 转化为 px
     * @param dp
     * @return
     */
    public static int dp2px(int dp) {
        //获取手机密度
        float density = getContext().getResources().getDisplayMetrics().density;
        //四舍五入
        return (int) (dp * density + 0.5);
    }

    public static int px2dp(int px) {
        //获取手机密度
        float density = getContext().getResources().getDisplayMetrics().density;
        //实现四舍五入
        return (int) (px / density + 0.5);
    }
}
