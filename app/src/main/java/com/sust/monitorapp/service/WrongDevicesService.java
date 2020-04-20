package com.sust.monitorapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.sust.monitorapp.R;
import com.sust.monitorapp.activity.DeviceDataActivity;
import com.sust.monitorapp.activity.WrongDevicesActivity;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.common.MyApplication;
import com.sust.monitorapp.util.CheckUtil;
import com.sust.monitorapp.util.JsonUtil;
import com.sust.monitorapp.util.NetUtil;
import com.sust.monitorapp.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.NotificationCompat;
import okhttp3.Response;

public class WrongDevicesService extends Service {

    //android 8.0之上显示通知需要填渠道id和name
    private final String CHANNEL_ID = "channel_id_1";
    private final String CHANNEL_NAME = "channel_name_1";

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("-----------service启动了--------");

        //初始化 service
        initService();
    }

    /**
     * 初始化 service
     */
    private void initService() {
        //循环计时器
        Timer timer = new Timer();

        /*
            每隔一定时间运行一次，一直重复执行
            参数含义：
            1. 一个TimerTask对象，里面是每次运行的内容
            2. 当你调用了timer.scheduleAtFixedRate()方法之后,这个方法就肯定会调用TimerTask()方法中的run()方法,
            这个参数指的是这两者之间的差值,也就是说用户在调用了scheduleAtFixedRate()方法之后,会等待0时间,才会第一次
            执行run()方法,0也就是代表无延迟了,如果传入其他的,就代表要延迟执行了
            3. 第一次调用了run()方法之后,从第二次开始每隔多长时间调用一次run()方法.
         */
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        }, 0, 3 * 60 * 1000);// 3min
    }

    /**
     * 获取数据异常的设备信息。
     * 有，就通知到前台；没有，什么都不做
     */
    private void getData() {
        new Thread(()->{
            //获取该用户权限能查看的所有设备
            String url = "/api/get_all_devs?userId=" + MyApplication.user.getUserId();
            try {
                Response response = NetUtil.get(url);
                if (response.isSuccessful()) {
                    //获取、检查每个设备的当前温度，有异常的放到一块
                    MyResponse myResponse = JsonUtil.jsonToBean(response.body().string(), MyResponse.class);
                    HashMap<String, String> idMacMap = JsonUtil.jsonToBean(myResponse.getData(),
                            new TypeToken<HashMap<String, String>>(){}.getType());

                    //存放异常设备
                    ArrayList<Device> deviceArrayList = new ArrayList<>();

                    for (Map.Entry<String, String> entry : idMacMap.entrySet()) {
                        String devId = entry.getKey();
                        String url1 = "/api/get_now_data?devId=" + devId;
                        Response response1 = NetUtil.get(url1);
                        if (response1.isSuccessful()) {
                            MyResponse myResponse1 = JsonUtil.jsonToBean(response1.body().string(), MyResponse.class);
                            //绕组，油面
                            float[] temData = JsonUtil.jsonToBean(myResponse1.getData(), float[].class);
                            //检查温度是否超标
                            if (!CheckUtil.raozuIsRight(temData[0]) || !CheckUtil.youmianIsRight(temData[1])) {
                                //获取超标的设备信息
//                                String url2 = "/api/get_dev_info?devId=" + devId;
                                String url2 = "https://www.fastmock.site/mock/f4ca486163cca9e79d548eb9c85770ec/api/get_dev_info?devId=" + devId;

                                Response response2 = NetUtil.urlget(url2);
                                if (response2.isSuccessful()) {
                                    MyResponse myResponse2 = JsonUtil.jsonToBean(response2.body().string(), MyResponse.class);
                                    Device device = JsonUtil.jsonToBean(myResponse2.getData(), Device.class);
                                    deviceArrayList.add(device);
                                }
                            }
                        }
                    }

                    //如果存在异常设备，就通知
                    if (!deviceArrayList.isEmpty()) {
                        pushMessage(deviceArrayList);
                    }

                } else {
                    Log.d("tag", "后台网络请求失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("定时器运转了");
    }

    /**
     * 通知栏通知有多少台异常设备，提供点击入口
     */
    private void pushMessage(ArrayList<Device> list) {
        String title = "电力设备数据异常";
        String msg = "有" + list.size() + "台电力设备异常，请立即点击查看 >>";

        //通知创建
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
            //通知才能正常弹出
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        //NotificationCompat.Builder 构造函数要求您提供渠道 ID。
        // 这是兼容 Android 8.0（API 级别 26）及更高版本所必需的，但会被较旧版本忽略。
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Intent intent = new Intent(WrongDevicesService.this, WrongDevicesActivity.class);
        intent.putExtra("wrong_devices", list.toArray(new Device[]{}));
        /*
        PendingIntent几个常量：
        1.FLAG_CANCEL_CURRENT ：如果AlarmManager管理的PendingIntent已经存在，那么将会取消当前的PendingIntent，
        从而创建一个新的PendingIntent
        2.FLAG_UPDATE_CURRENT：如果AlarmManager管理的PendingIntent已经存在，可以让新的Intent更新之前
        PendingIntent中的Intent对象数据，例如更新Intent中的Extras，另外，我们也可以在PendingIntent的原进程中
        调用PendingIntent的cancel ()把其从系统中移除掉
        3.FLAG_NO_CREATE ：如果AlarmManager管理的PendingIntent已经存在，那么将不进行任何操作，直接返回已经存在
        的PendingIntent，如果PendingIntent不存在了，那么返回null
         */
        PendingIntent pi = PendingIntent.getActivity(WrongDevicesService.this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.mipmap.icon_app)
                .setContentTitle(title)// 通知栏标题
                .setContentText(msg)// 通知栏内容
                .setTicker(msg)// 没有下拉时的浮动显示
                .setWhen(System.currentTimeMillis())// 时间
                .setDefaults(Notification.DEFAULT_LIGHTS) //灯光
                .setDefaults(Notification.DEFAULT_VIBRATE)// 震动
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //系统默认的铃声
                .setOngoing(false)//可以删除
                .setAutoCancel(true)//点击后消失
                .setContentIntent(pi); //点击跳转到

        mNotificationManager.notify(0, builder.build());

    }
}
