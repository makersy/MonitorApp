package com.sust.monitorapp.common;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.sust.monitorapp.bean.User;


/**
 * Created by yhl on 2020/2/25.
 */

public class MyApplication extends Application {

    /**
     * 在整个应用执行过程中，需要提供的变量
     */
    //需要使用的上下文对象
    public static Context context;
    //需要使用的Handler
    public static Handler handler;
    //提供主线程对象
    public static Thread mainThread;
    //提供主线程对象id
    public static int mainThreadId;

    //当前登录用户
    public static User user;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();
        handler = new Handler();
        //实例化当前Application的线程即为主线程
        mainThread = Thread.currentThread();
        //获取当前主线程的id
        mainThreadId = android.os.Process.myTid();
    }
}